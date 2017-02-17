package com.a.eye.skywalking.trace;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link TraceSegment} is a segment or fragment of the distributed trace.
 * {@see https://github.com/opentracing/specification/blob/master/specification.md#the-opentracing-data-model}
 * A {@link
 * TraceSegment} means the segment, which exists in current {@link Thread}. And the distributed trace is formed by multi
 * {@link TraceSegment}s, because the distributed trace crosses multi-processes, multi-threads.
 *
 * Created by wusheng on 2017/2/17.
 */
public class TraceSegment {
    /**
     * The id of this trace segment.
     * Every segment has its unique-global-id.
     */
    private String traceSegmentId;

    /**
     * The start time of this trace segment.
     */
    private long startTime;

    /**
     * The end time of this trace segment.
     */
    private long endTime;

    /**
     * The primary ref of the parent trace segment.
     * Use {@link TraceSegmentRef}, we can link this trace segment to the primary parent segment.
     */
    private TraceSegmentRef primaryRef;

    /**
     * The refs of other parent trace segments, except the primary one.
     * For most RPC call, {@link #refs} stay in null,
     * but if this segment is a start span of batch process, the segment faces multi parents,
     * at this moment, we use this {@link #refs} to link them.
     */
    private List<TraceSegmentRef> refs;

    /**
     * The spans belong to this trace segment.
     * They all have finished.
     * All active spans are hold and controlled by "skywalking-api" module.
     */
    private List<Span> spans;

    /**
     * Create a trace segment, by given segmentId.
     * This segmentId is generated by TraceSegmentRef, AKA, from tracer/agent module.
     *
     * @param segmentId {@link #traceSegmentId}
     */
    public TraceSegment(String segmentId) {
        this.traceSegmentId = segmentId;
        this.startTime = System.currentTimeMillis();
        this.spans = new LinkedList<Span>();
    }

    /**
     * Establish the link between this segment and its parents.
     * The first time, you {@link #ref(TraceSegmentRef)} to parent, it is affirmed as {@link #primaryRef}.
     * And others are affirmed as {@link #refs}.
     *
     * @param refSegment {@link TraceSegmentRef}
     */
    public void ref(TraceSegmentRef refSegment){
        if(primaryRef == null){
            primaryRef = refSegment;
        }else {
            if (refs == null) {
                refs = new LinkedList<TraceSegmentRef>();
            }
            refs.add(refSegment);
        }
    }

    /**
     * After {@link Span} is finished, as be controller by "skywalking-api" module,
     * notify the {@link TraceSegment} to archive it.
     *
     * @param finishedSpan
     */
    void archive(Span finishedSpan){
        spans.add(finishedSpan);
    }

    /**
     * Finish this {@link TraceSegment}.
     */
    public void finish(){
       this.endTime = System.currentTimeMillis();
    }

    public String getTraceSegmentId() {
        return traceSegmentId;
    }
}
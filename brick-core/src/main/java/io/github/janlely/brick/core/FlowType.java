package io.github.janlely.brick.core;

/**
 * the flow type
 */
public enum FlowType {
    /**
     * pure
     */
    PURE_FUNCTION,
    /**
     * effect
     */
    EFFECT,
    /**
     * branch
     */
    BRANCH,
    /**
     * abort
     */
    ABORT,
    /**
     * loop
     */
    LOOP,
    /**
     * sub flow
     */
    SUB_FLOW,
    /**
     * trace
     */
    TRACE_FLOW,
    /**
     * foldl
     */
    FOLDL_FLOW,
    /**
     * count down
     */
    COUNT_DOWN,
    /**
     * context modify
     */
    CONTEXT_MODIFY,
    /**
     * const
     */
    CONST_FLOW,
    /**
     * map
     */
    MAP_FLOW;
}
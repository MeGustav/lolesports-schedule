package com.megustav.lolesports.schedule.processor;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Processor type
 *
 * @author MeGustav
 *         15/02/2018 22:26
 */
public enum ProcessorType {

    /** Full schedule on an upcoming/ongoing week */
    FULL_SCHEDULE("/full"),
    /** Current teams standings */
    STANDINGS("/standings"),
    /** Closest matches to come */
    IMMEDIATE_SCHEDULE("/immediate");

    /** Processor corresponding path */
    private String path;

    ProcessorType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    /**
     * Retrieve corresponding {@link ProcessorType}
     *
     * @param request request text
     * @return {@link ProcessorType}
     */
    public static Optional<ProcessorType> fromRequest(String request) {
        return Stream.of(values())
                // Getting type matching the request
                .filter(type -> request.startsWith(type.getPath()))
                .findAny();
    }
}

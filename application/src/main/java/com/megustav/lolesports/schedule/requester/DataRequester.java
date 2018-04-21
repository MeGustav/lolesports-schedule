package com.megustav.lolesports.schedule.requester;

/**
 * Contract for getting data with a request
 *
 * @param <Req> type of request
 * @param <Res> type of response data
 *
 * @author MeGustav
 *         20/04/2018 00:43
 */
public interface DataRequester<Req, Res> {

    /**
     * Request data
     *
     * @param request request
     * @return response data
     */
    Res requestData(Req request);

}

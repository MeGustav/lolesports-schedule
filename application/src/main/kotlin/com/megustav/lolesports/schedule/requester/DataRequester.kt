package com.megustav.lolesports.schedule.requester

interface DataRequester<Req, Res> {
    fun requestData(request: Req): Res
}
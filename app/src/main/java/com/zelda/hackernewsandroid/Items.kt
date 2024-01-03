package com.zelda.hackernewsandroid

class Items(
    // from API
    var id: Long,
    var deleted: Boolean? = null,
    var type: String? = null,
    var by: String? = null,
    var time: Long? = null,
    var text: String? = null,
    var dead: Boolean? = null,
    var parent: Long? = null,
    var poll: Long? = null,
    var kids: List<Long>? = null,
    var url: String? = null,
    var score: Int? = null,
    var title: String? = null,
    var parts: List<Long>? = null,
    var descendants: Int? = null,

    // defined by me, this would be fetched through News's url
    // especially for News Item
    var context: String,
)
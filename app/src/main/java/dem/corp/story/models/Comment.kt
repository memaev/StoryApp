package dem.corp.story.models

import dem.corp.story.repository.firebase.CHILD_FROM
import dem.corp.story.repository.firebase.CHILD_TEXT
import dem.corp.story.repository.firebase.CHILD_TITLE

class Comment {
    var text: String? = null
    var username: String? = null
    var date: String? = null
    var from: String? = null

    constructor(text: String?, username: String?, date: String?, from: String?) {
        this.text = text
        this.username = username
        this.date = date
        this.from = from
    }

    constructor() {}
}
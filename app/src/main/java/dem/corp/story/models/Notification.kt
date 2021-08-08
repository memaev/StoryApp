package dem.corp.story.models

import java.net.IDN

class Notification {
    var type : String? = null
    var from: String? = null
    var username: String? = null
    var storyID: String? = null
    var date: String? = null
    var text: String? = null

    constructor(type: String?, from: String?, username: String?, storyID: String?, date: String?, text: String?) {
        this.type = type
        this.from = from
        this.username = username
        this.storyID = storyID
        this.date = date
        this.text = text
    }

    constructor() {}
}
package dem.corp.story.models

class Comment {
    var text: String? = null
    var username: String? = null
    var date: String? = null

    constructor(text: String?, username: String?, date: String?) {
        this.text = text
        this.username = username
        this.date = date
    }

    constructor() {}
}
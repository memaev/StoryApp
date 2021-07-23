package dem.corp.story.story

class Story {
    var text: String? = null
    var from: String? = null
    var title: String? = null

    constructor(text: String?, from: String?, title: String?) {
        this.text = text
        this.from = from
        this.title = title
    }

    constructor() {}
}
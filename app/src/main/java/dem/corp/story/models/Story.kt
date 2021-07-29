package dem.corp.story.models

import dem.corp.story.repository.firebase.AUTH
import dem.corp.story.repository.firebase.CHILD_FROM
import dem.corp.story.repository.firebase.CHILD_TEXT
import dem.corp.story.repository.firebase.CHILD_TITLE

data class Story(
        var text: String = "",
        var from: String = AUTH.uid.toString(),
        var title: String = "",
        var id: String = ""
) {
    fun asHashMap(): HashMap<String, String> {
        val map = HashMap<String, String>()

        map[CHILD_TEXT] = this.text
        map[CHILD_FROM] = this.from
        map[CHILD_TITLE] = this.title

        return map
    }
}



/*
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
 */

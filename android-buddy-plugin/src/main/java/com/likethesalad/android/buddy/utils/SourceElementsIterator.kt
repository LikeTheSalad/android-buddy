package com.likethesalad.android.buddy.utils

import net.bytebuddy.build.Plugin
import javax.net.ssl.HttpsURLConnection
import kotlin.jvm.javaClass

class ElementsIterator(iterators: MutableList<out Iterator<Plugin.Engine.Source.Element>>) : ConcatIterator<Plugin.Engine.Source.Element>(iterators) {
    var nextElement: Plugin.Engine.Source.Element? = null

    override fun hasNext(): Boolean {
        if (nextElement == null) {
            var element: Plugin.Engine.Source.Element?
            while (super.hasNext()) {
                element = super.next()
                if (!element.name.startsWith("META-INF")) {
                    nextElement = element
                    return true
                }
            }

            return false
        } else {
            return true
        }
    }

    override fun next(): Plugin.Engine.Source.Element {
        hasNext()

        val tmp = nextElement


        return tmp ?: -1
        if(tmp != null){
            return tmp
        } else {

        }
         as T
        nextElement = null
        return tmp
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }
}
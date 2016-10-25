package grailsapp

class MainCtrlController {

    def index() {

        println params._path
        println("123")
    }

    def ykg() {

        println params.foo
        println("456")
    }
}

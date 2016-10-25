class UrlMappings {

	static mappings = {
//        "/$controller/$action?/$id?(.$format)?"{
//            constraints {
//                // apply constraints here
//            }
//        }

        "/b/$_path"(controller: 'mainCtrl') {
            action = [GET: 'index']
            parseRequest = true
        }

        "/tx"(view:"/index")
        "500"(view:'/error')
	}
}

rootProject.name = "udm-documents"

// Business components
include(":filestore")
include(":filestore:adapter:in:web-mvc")
include(":filestore:adapter:out:db-jdbi")
include(":filestore:adapter:out:file")

include(":folders")
include(":folders:adapter:in:web-mvc")
include(":folders:adapter:out:db-jdbi")

// API
include(":api")

// Runnable application
include(":configuration")
include(":configuration:web-api")

rootProject.name = "udm-documents"

// Business components
include(":filestore")
include(":filestore:adapter:in:web-mvc")
include(":filestore:adapter:out:db-jdbi")
include(":filestore:adapter:out:file")

// API
include(":api")

// Runnable application
include(":configuration")
include(":configuration:web-api")

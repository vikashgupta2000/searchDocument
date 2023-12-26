# Search Document
- **Author**: Vikash Gupta

Design and build an application that can search documents from a cloud storage service like Dropbox or Google Drive on 
the content inside the document.

## Overview

This project leverages Elasticsearch for faster and more efficient search capabilities and integrates with Dropbox for 
persistent storage of files. Elasticsearch is a powerful open-source search and analytics engine, while Dropbox provides reliable cloud storage.

In the course of this project, we are incorporating Dropbox integration. However, in the event that the need arises, 
we can seamlessly integrate with any cloud storage platform with minimal adjustments to the project.

## Features

- **Elasticsearch Integration**: Enhance your search functionality by using Elasticsearch for real-time searching and indexing of large datasets.

- **Dropbox Integration**: Leverage Dropbox for persistent storage of files. Easily read and manage files stored in the cloud.

## Getting Started

Follow these steps to set up and use Elasticsearch and Dropbox for your project:

### Prerequisites

- **Java version**: Please install Java 17 in the system

- **Elasticsearch Server**: Ensure you have an Elasticsearch server running. Utilize the following Docker command to initiate the Elasticsearch server locally.

    
    docker run --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" -t docker.elastic.co/elasticsearch/elasticsearch:8.11.3

Update the hostname and port of elasticsearch in application.properties along with the api_key. 

- **Docker Integration**: Update the access_token in application.properties to fetch the files from dropBox

### Run
To run the microservice please execure the below command

    
    mvn clean spring-boot:run


## API

- **search API**: This API will help the user to search the complete text passed


    http://localhost:8080/client/126/search?q=reference

- **refresh API**: This API will help the user to refresh the file data from the storate to be indexed in ElasticSearch. 
The file will be updated only if there is a change in file content.

    
    http://localhost:8080/client/126/refresh


## Approach
- **Multi-Tenant Architecture**: The application is structured to manage data in a multi-tenant environment, with the 
Elasticsearch index corresponding to the client_id. Currently, no security layer has been implemented. However, the plan
is to introduce an authorization system where client details will be accessible only after the authorization process is successfully completed.

- **Seamless Integration**: Presently, the application is integrated with Dropbox, but it can seamlessly expand its 
functionalities to integrate with other storage systems. Since the Elasticsearch index is structured around the client_id, 
data from different persistent storage sources can be indexed, facilitating retrieval with a single call. 
Currently, a shared access token is used for all clients; however, the plan is to store access tokens per client in a SQL 
database. This way, each client can specify the storage systems they want the system to access.

- **Text Extraction**:  In the preliminary implementation, the solution focuses on extracting information solely from text 
files. However, future enhancements can involve incorporating text extractions from various file types. The framework is 
designed to accommodate additional file type handlers without necessitating modifications to the core structure. Given 
the independence of file storage and text extraction processes, scaling the system in a generic manner becomes straightforward for developers.



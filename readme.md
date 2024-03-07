# UnixSearch

UnixSearch is a Java Spring project that periodically downloads data dumps related to Unix from various sources such as Stack Exchange and Wikipedia. The collected data is then pushed or indexed into an Elasticsearch container using a scheduler. The project also provides an API that allows users to perform searches on the indexed data, with support for caching.

## Features

- Periodic data download from Stack Exchange and Wikipedia
- Indexing of downloaded data into Elasticsearch
- Scheduler for automated data retrieval and indexing
- API for searching indexed data with caching support

## Setup

### Prerequisites

1. Java Development Kit (JDK) 17 or later
2. Apache Maven
3. `unix_wiki.xml` and `unix_stackexchange.xml` data dump files downloaded and placed in the `./downloads` project directory.
4. Docker desktop

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/vedantsahu01/UnixSearch.git
   cd UnixSearch
   ```

2. **Download and place data dump files:**
    - Download `unix_wiki.xml` and `unix_stackexchange.xml` files.
    - Place these files in the `./downloads` project directory.

3. **run docker compose:**
    - Start docker.
    - Run command `docker-compose up -d`

4. **Build the project:**
   ```bash
   mvn clean install
   ```


6. **Run the project**

Now, your UnixSearch project is up and running! The scheduler will periodically download data dumps and index them into Elasticsearch.

## API Contract

### Search API

Perform a search on the indexed data.

**Endpoint:** `http://localhost:8080/api/es/search`

**Method:** `POST`

**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "searchTerm": "file",
  "fields": [],
  "filters": [],
  "sort": {
    "field": "creationDate",
    "sortType": "ASC"
  }
}
```

**Response:**
```json
{
  "staxPostList": [
    {
      "id": 298419,
      "postTypeId": 1,
      "acceptedAnswerId": 298443,
      "creationDate": "2016-07-26T16:42:35.560",
      "score": 4,
      "viewCount": 1511,
      "body": "=[m ]</p>\n",
      "ownerUserId": 181603,
      "lastEditorUserId": 885,
      "lastEditDate": "2016-07-26T22:05:10.570",
      "lastActivityDate": "2016-07-26T22:05:10.570",
      "title": "how can I read a number with file (magic)?",
      "tags": "<file-command><file-types>",
      "answerCount": 1,
      "commentCount": 0,
      "contentLicense": "CC BY-SA 3.0"
    }
  ],
  "wikiPostList": [{}]
}
```

Feel free to contribute, report issues, or suggest improvements. Happy searching!
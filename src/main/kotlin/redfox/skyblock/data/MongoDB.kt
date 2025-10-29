package redfox.skyblock.data

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase

object MongoDB {
    val client: MongoClient = MongoClients.create("mongodb://localhost:27017")
    val db: MongoDatabase = client.getDatabase("redfox")
}
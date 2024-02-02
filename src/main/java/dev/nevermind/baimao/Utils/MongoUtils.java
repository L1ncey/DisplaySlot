package dev.nevermind.baimao.Utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import dev.nevermind.baimao.Main;
import lombok.experimental.UtilityClass;
import org.bson.Document;

@UtilityClass
public final class MongoUtils {
    public static final UpdateOptions UPSERT_OPTIONS = new UpdateOptions().upsert(true);

    public static MongoCollection<Document> getCollection(String collectionId) {
        return Main.getInstance().getMongoDatabase().getCollection(collectionId);
    }

}
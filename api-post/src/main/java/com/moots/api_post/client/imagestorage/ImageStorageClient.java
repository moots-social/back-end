package com.moots.api_post.client.imagestorage;

import java.io.IOException;
import java.io.InputStream;

public interface ImageStorageClient {

    String uploadImage(String containerName, String originalImageName, InputStream data, Long length) throws IOException;

}

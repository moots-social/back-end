package com.api.usuario_post.client.imagestorage;

import com.api.usuario_post.handler.BlobException;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureImageStorageClient implements ImageStorageClient{

    private static final Logger log = LoggerFactory.getLogger(AzureImageStorageClient.class);
    @Autowired
    private BlobServiceClient blobServiceClient;

    @Override
    public String uploadImage(String containerName, String originalImageName, InputStream data, Long length) throws IOException {

        try {
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);


            String newImageName = UUID.randomUUID().toString() + originalImageName.substring(originalImageName.lastIndexOf("."));

            BlobClient blobClient =  blobContainerClient.getBlobClient(newImageName);

            blobClient.upload(data, length, true);

            return blobClient.getBlobUrl();
        } catch (BlobException e){
            throw new BlobException("Falha ao upar a imagem no Blob storage", e.getCause());
        }

    }

    @Override
    public void deleteBlob(String blobName, String containerName) {
       try{
           blobServiceClient.getBlobContainerClient(containerName).getBlobClient(blobName).delete();
       }catch (BlobException e){
           throw new BlobException("Falha ao deletar o blob", e.getCause());
       }
    }

}

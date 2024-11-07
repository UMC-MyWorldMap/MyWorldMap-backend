package umc.TripPiece.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.config.AmazonConfig;
import umc.TripPiece.domain.Uuid;
import umc.TripPiece.payload.code.status.ErrorStatus;
import umc.TripPiece.payload.exception.handler.PictureHandler;
import umc.TripPiece.payload.exception.handler.VideoHandler;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    // 여러장의 파일 저장
    public List<String> saveFiles(List<String> keyNames, List<MultipartFile> files) {
        int fileNum = files.size();
        List urls = new ArrayList();

        for (int i=0;i<fileNum;i++) {

            ObjectMetadata metadata = new ObjectMetadata();
            MultipartFile file = files.get(i);
            String keyName = keyNames.get(i);
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            try {
                amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
            } catch (IOException e){
                log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
            }

            urls.add(amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString());
        }

        return urls;
    }

    // 단일 파일 저장
    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        }catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public void deleteFile(String keyName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), keyName));
            log.info("Successfully deleted file: {}", keyName);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", keyName, e);
            throw new IllegalStateException("Failed to delete file from S3", e);
        }
    }


    public String generateTripPieceKeyName(Uuid uuid) {
        return amazonConfig.getTripPiecePath() + '/' + uuid.getUuid();
    }

    public List<String> generateTripPieceKeyNames(List<Uuid> uuids) {
        List<String> keyNames = new ArrayList<>();

        for(Uuid uuid : uuids) {
            String keyName = amazonConfig.getTripPiecePath() + '/' + uuid.getUuid();
            keyNames.add(keyName);
        }
        return keyNames;
    }


}
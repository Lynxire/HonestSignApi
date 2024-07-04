package terabu;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class CrptApi {
    private final int requestLimit;
    private final long intervalInMillis;
    private final Lock lock = new ReentrantLock();
    private long lastRequestTime = System.currentTimeMillis();
    private int requestCount = 0;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.requestLimit = requestLimit;
        this.intervalInMillis = timeUnit.toMillis(1);
    }

    public boolean allowRequest() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastRequestTime >= intervalInMillis) {
                requestCount = 0;
                lastRequestTime = currentTime;
            }
            if (requestCount < requestLimit) {
                requestCount++;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }


    public void createDoc(GoodsDTO goodsDTO, String signature) throws IOException, InterruptedException {
        final String trueSignature = "Test Signature";
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(goodsDTO);

        if (signature.equals(trueSignature)) {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            System.out.println(json);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("Ответ от сервера: " + responseBody);
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ProductDTO product = new ProductDTO();
        product.setCertificateDocument("Сертификат товара");
        product.setCertificateDocumentDate("2020-01-23");
        product.setCertificateDocumentNumber("CERT-456");
        product.setOwnerInn("1234567890");
        product.setProducerInn("5432109876");
        product.setProductionDate("2020-01-23");
        product.setTnvedCode("123456");
        product.setUitCode("UIT-789");
        product.setUituCode("UITU-987");

        GoodsDTO goods = new GoodsDTO();
        goods.setDescription("Описание товара");
        goods.setDocId("12345");
        goods.setDocStatus("APPROVED");
        goods.setDocType("LP_INTRODUCE_GOODS");
        goods.setImportRequest(true);
        goods.setOwnerInn("1234567890");
        goods.setParticipantInn("9876543210");
        goods.setProducerInn("5432109876");
        goods.setProductionDate("2020-01-23");
        goods.setProductionType("Тип производства");
        goods.setProducts(List.of(product));
        goods.setRegDate("2020-01-23");
        goods.setRegNumber("REG-123");


        CrptApi api = new CrptApi(TimeUnit.SECONDS, 100);
        if (api.allowRequest()) {
            api.createDoc(goods, "Test Signature");
        } else {
            System.out.println("Запрос заблокирован");
        }





    }

}

class GoodsDTO {
    private String description;
    private String docId;
    private String docStatus;
    private String docType;
    private boolean importRequest;
    private String ownerInn;
    private String participantInn;
    private String producerInn;
    private String productionDate;
    private String productionType;
    private List<ProductDTO> products;
    private String regDate;
    private String regNumber;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsDTO goodsDTO = (GoodsDTO) o;
        return importRequest == goodsDTO.importRequest && Objects.equals(description, goodsDTO.description) && Objects.equals(docId, goodsDTO.docId) && Objects.equals(docStatus, goodsDTO.docStatus) && Objects.equals(docType, goodsDTO.docType) && Objects.equals(ownerInn, goodsDTO.ownerInn) && Objects.equals(participantInn, goodsDTO.participantInn) && Objects.equals(producerInn, goodsDTO.producerInn) && Objects.equals(productionDate, goodsDTO.productionDate) && Objects.equals(productionType, goodsDTO.productionType) && Objects.equals(products, goodsDTO.products) && Objects.equals(regDate, goodsDTO.regDate) && Objects.equals(regNumber, goodsDTO.regNumber);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(docId);
        result = 31 * result + Objects.hashCode(docStatus);
        result = 31 * result + Objects.hashCode(docType);
        result = 31 * result + Boolean.hashCode(importRequest);
        result = 31 * result + Objects.hashCode(ownerInn);
        result = 31 * result + Objects.hashCode(participantInn);
        result = 31 * result + Objects.hashCode(producerInn);
        result = 31 * result + Objects.hashCode(productionDate);
        result = 31 * result + Objects.hashCode(productionType);
        result = 31 * result + Objects.hashCode(products);
        result = 31 * result + Objects.hashCode(regDate);
        result = 31 * result + Objects.hashCode(regNumber);
        return result;
    }

    @Override
    public String toString() {
        return "GoodsDTO{" +
                "description='" + description + '\'' +
                ", docId='" + docId + '\'' +
                ", docStatus='" + docStatus + '\'' +
                ", docType='" + docType + '\'' +
                ", importRequest=" + importRequest +
                ", ownerInn='" + ownerInn + '\'' +
                ", participantInn='" + participantInn + '\'' +
                ", producerInn='" + producerInn + '\'' +
                ", productionDate='" + productionDate + '\'' +
                ", productionType='" + productionType + '\'' +
                ", products=" + products +
                ", regDate='" + regDate + '\'' +
                ", regNumber='" + regNumber + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public boolean isImportRequest() {
        return importRequest;
    }

    public void setImportRequest(boolean importRequest) {
        this.importRequest = importRequest;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public void setOwnerInn(String ownerInn) {
        this.ownerInn = ownerInn;
    }

    public String getParticipantInn() {
        return participantInn;
    }

    public void setParticipantInn(String participantInn) {
        this.participantInn = participantInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public void setProducerInn(String producerInn) {
        this.producerInn = producerInn;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getProductionType() {
        return productionType;
    }

    public void setProductionType(String productionType) {
        this.productionType = productionType;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
}

class ProductDTO {
    private String certificateDocument;
    private String certificateDocumentDate;
    private String certificateDocumentNumber;
    private String ownerInn;
    private String producerInn;
    private String productionDate;
    private String tnvedCode;
    private String uitCode;
    private String uituCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDTO that = (ProductDTO) o;
        return Objects.equals(certificateDocument, that.certificateDocument) && Objects.equals(certificateDocumentDate, that.certificateDocumentDate) && Objects.equals(certificateDocumentNumber, that.certificateDocumentNumber) && Objects.equals(ownerInn, that.ownerInn) && Objects.equals(producerInn, that.producerInn) && Objects.equals(productionDate, that.productionDate) && Objects.equals(tnvedCode, that.tnvedCode) && Objects.equals(uitCode, that.uitCode) && Objects.equals(uituCode, that.uituCode);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(certificateDocument);
        result = 31 * result + Objects.hashCode(certificateDocumentDate);
        result = 31 * result + Objects.hashCode(certificateDocumentNumber);
        result = 31 * result + Objects.hashCode(ownerInn);
        result = 31 * result + Objects.hashCode(producerInn);
        result = 31 * result + Objects.hashCode(productionDate);
        result = 31 * result + Objects.hashCode(tnvedCode);
        result = 31 * result + Objects.hashCode(uitCode);
        result = 31 * result + Objects.hashCode(uituCode);
        return result;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "certificateDocument='" + certificateDocument + '\'' +
                ", certificateDocumentDate='" + certificateDocumentDate + '\'' +
                ", certificateDocumentNumber='" + certificateDocumentNumber + '\'' +
                ", ownerInn='" + ownerInn + '\'' +
                ", producerInn='" + producerInn + '\'' +
                ", productionDate='" + productionDate + '\'' +
                ", tnvedCode='" + tnvedCode + '\'' +
                ", uitCode='" + uitCode + '\'' +
                ", uituCode='" + uituCode + '\'' +
                '}';
    }

    public String getCertificateDocument() {
        return certificateDocument;
    }

    public void setCertificateDocument(String certificateDocument) {
        this.certificateDocument = certificateDocument;
    }

    public String getCertificateDocumentDate() {
        return certificateDocumentDate;
    }

    public void setCertificateDocumentDate(String certificateDocumentDate) {
        this.certificateDocumentDate = certificateDocumentDate;
    }

    public String getCertificateDocumentNumber() {
        return certificateDocumentNumber;
    }

    public void setCertificateDocumentNumber(String certificateDocumentNumber) {
        this.certificateDocumentNumber = certificateDocumentNumber;
    }

    public String getOwnerInn() {
        return ownerInn;
    }

    public void setOwnerInn(String ownerInn) {
        this.ownerInn = ownerInn;
    }

    public String getProducerInn() {
        return producerInn;
    }

    public void setProducerInn(String producerInn) {
        this.producerInn = producerInn;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getTnvedCode() {
        return tnvedCode;
    }

    public void setTnvedCode(String tnvedCode) {
        this.tnvedCode = tnvedCode;
    }

    public String getUitCode() {
        return uitCode;
    }

    public void setUitCode(String uitCode) {
        this.uitCode = uitCode;
    }

    public String getUituCode() {
        return uituCode;
    }

    public void setUituCode(String uituCode) {
        this.uituCode = uituCode;
    }
}



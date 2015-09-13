// jp.co.sharp.openapi.cocorobo.IOpenApiCocoroboService.aidl.aidl


// Declare any non-default types here with import statements

interface IOpenApiCocoroboService {
  String control(String apiKey, String mode);
  String getData(String apiKey, String dataKind);
}

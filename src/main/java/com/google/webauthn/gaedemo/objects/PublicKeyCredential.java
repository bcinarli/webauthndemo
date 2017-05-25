package com.google.webauthn.gaedemo.objects;

import com.google.gson.Gson;

public class PublicKeyCredential {
  public String id;
  public String type;
  public byte[] rawId;
  AuthenticatorResponse response;

  /**
   * @param id
   * @param type
   * @param rawId
   * @param response
   */
  public PublicKeyCredential(String id, String type, byte[] rawId, AuthenticatorResponse response) {
    this.id = id;
    this.type = type;
    this.rawId = rawId;
    this.response = response;
  }

  /**
   * 
   */
  public PublicKeyCredential() {}

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @return the rawId
   */
  public byte[] getRawId() {
    return rawId;
  }

  public AttestationStatementEnum getAttestationType() {
    try {
      AuthenticatorAttestationResponse attRsp = (AuthenticatorAttestationResponse) response;
      AttestationStatement attStmt = attRsp.decodedObject.getAttestationStatement();
      if (attStmt instanceof AndroidSafetyNetAttestationStatement) {
        return AttestationStatementEnum.ANDROIDSAFETYNET;
      } else if (attStmt instanceof FidoU2fAttestationStatement) {
        return AttestationStatementEnum.FIDOU2F;
      }
    } catch (ClassCastException e) {
      return null;
    }
    return null;
  }

  /**
   * @return the response
   */
  public AuthenticatorResponse getResponse() {
    return response;
  }

  /**
   * @return json encoded String representation of PublicKeyCredential
   */
  public String encode() {
    return new Gson().toJson(this);
  }


}

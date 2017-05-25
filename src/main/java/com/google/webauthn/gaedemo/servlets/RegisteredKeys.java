package com.google.webauthn.gaedemo.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.io.BaseEncoding;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.webauthn.gaedemo.crypto.Crypto;
import com.google.webauthn.gaedemo.exceptions.WebAuthnException;
import com.google.webauthn.gaedemo.objects.AttestationObject;
import com.google.webauthn.gaedemo.objects.AuthenticatorAttestationResponse;
import com.google.webauthn.gaedemo.objects.EccKey;
import com.google.webauthn.gaedemo.objects.FidoU2fAttestationStatement;
import com.google.webauthn.gaedemo.storage.Credential;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegisteredKeys
 */
public class RegisteredKeys extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private final UserService userService = UserServiceFactory.getUserService();

  /**
   * @see HttpServlet#HttpServlet()
   */
  public RegisteredKeys() {}

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String currentUser = userService.getCurrentUser().getUserId();
    List<Credential> savedCreds = Credential.load(currentUser);

    JsonArray result = new JsonArray();
    response.setContentType("text/json");
    for (Credential c : savedCreds) {
      JsonObject cJson = new JsonObject();
      cJson.addProperty("handle", BaseEncoding.base64().encode(c.getCredential().rawId));
      EccKey ecc = (EccKey) ((AuthenticatorAttestationResponse) c.getCredential()
          .getResponse()).decodedObject.getAuthenticatorData().getAttData().getPublicKey();
      try {
        cJson.addProperty("publicKey",
            Integer.toHexString(Crypto.decodePublicKey(ecc.getX(), ecc.getY()).hashCode()));
      } catch (WebAuthnException e) {
        // TODO(piperc): Auto-generated catch block
        e.printStackTrace();
      }
      AttestationObject attObj =
          ((AuthenticatorAttestationResponse) c.getCredential().getResponse())
              .getAttestationObject();
      if (attObj.getAttestationStatement() instanceof FidoU2fAttestationStatement) {
        cJson.addProperty("name", "FIDO U2F Authenticator");
      }
      cJson.addProperty("date", c.getDate().toString());
      cJson.addProperty("id", c.id);
      result.add(cJson);
    }
    response.getWriter().print(result.toString());
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doGet(request, response);
  }

}

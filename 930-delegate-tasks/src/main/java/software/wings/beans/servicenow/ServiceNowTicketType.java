package software.wings.beans.servicenow;

import lombok.Getter;

public enum ServiceNowTicketType {
  INCIDENT("Incident"),
  PROBLEM("Problem"),
  CHANGE_REQUEST("Change"),
  CHANGE_TASK("Change Task");
  @Getter private String displayName;
  ServiceNowTicketType(String s) {
    displayName = s;
  }
}

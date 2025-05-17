package DomainLayer.Enums;

public enum OrderStatus {
   PENDING("Pending"),
   APPROVED("Approved"),
   REJECTED("Rejected"),
   COMPLETED("Completed");

   private final String displayName;

   OrderStatus(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayName() {
      return displayName;
   }

   public String toString() {
      return displayName;
   }
}

package DomainLayer.Enums;

public enum WeekofDay {
   SUNDAY("Sunday"),
   MONDAY("Monday"),
   TUESDAY("Tuesday"),
   WEDNESDAY("Wednesday"),
   THURSDAY("Thursday"),
   FRIDAY("Friday"),
   SATURDAY("Saturday");

   private final String displayName;

   WeekofDay(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayName() {
      return displayName;
   }

   @Override
   public String toString() {
      return displayName;
   }
}

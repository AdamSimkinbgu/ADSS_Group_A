import PresentationLayer.AppCLI;

public class Main {
   public static void main(String[] args) {
      // we should change this so that the data path is optional
      String[] fakeargs = new String[] { "dev/data.json" }; // For testing purposes
      if (fakeargs.length != 1 || fakeargs[0].isEmpty()) {
         System.err.println("Please provide the data path as an argument.");
         System.exit(1);
      } else if (fakeargs[0].equals("help") && fakeargs.length == 1) {
         System.out.println("Help: Provide the data path as an argument.");
         System.out.println("Usage: java AppCLI <dataPath>");
         System.out.println("Example: java AppCLI /path/to/data");
         System.exit(0);
      } else if (!fakeargs[0].isEmpty() && fakeargs.length == 1) {
         System.out.println("Data path provided: " + fakeargs[0]);
      }

      AppCLI app = new AppCLI(fakeargs[0]);
   }
}

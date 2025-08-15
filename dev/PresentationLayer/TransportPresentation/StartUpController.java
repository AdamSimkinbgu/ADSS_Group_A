package PresentationLayer.TransportPresentation;

//import ServiceLayer.TranEmployeeService;
import ServiceLayer.TransportServices.StartUpStateService;

public class StartUpController {
    private StartUpStateService startUpStateService;

    public StartUpController(StartUpStateService startUpStateService) {
        this.startUpStateService = startUpStateService;
    }

    public void startUpData(){
        String res = startUpStateService.loadData();
//        if(res.equals("SUCCESS")){
//            System.out.println("\nSuccessfully Finished Loading Starting Data.\n");
//        }else{
//            System.out.println("\nFailed Loading Starting Data\n\n");
//        }
//        System.out.println("\nStarting the System...\n\n");
    }


}




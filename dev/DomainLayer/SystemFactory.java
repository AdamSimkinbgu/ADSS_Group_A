package DomainLayer;

import DomainLayer.EmployeeSubModule.*;
import DomainLayer.EmployeeSubModule.Repository.*;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.BranchRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.EmployeeRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.SuppliersDomainSubModule.OrderFacade;
import DomainLayer.SuppliersDomainSubModule.SupplierFacade;
import DomainLayer.TransportDomain.SiteSubModule.SiteFacade;
import DomainLayer.TransportDomain.TransportSubModule.TransportController;
import DomainLayer.TransportDomain.TruckSubModule.TruckFacade;
import ServiceLayer.*;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.TransportServices.*;
import PresentationLayer.EmployeeSubModule.CLI.HR_MainCLI;
import PresentationLayer.TransportPresentation.MainTranSysCLI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import DTOs.SuppliersModuleDTOs.Enums.InitializeState;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Factory class for creating and initializing system components.
 * Implements the Factory Pattern to encapsulate the creation logic.
 */
public class SystemFactory {
    public SystemFactory() {
        // No initialization needed
    }

    /**
     * Creates and initializes the Employee Module components.
     * 
     * @param minimalMode If true, initializes with minimal data (admin only).
     * @return EmployeeModuleComponents containing all initialized components.
     * @throws IOException If there's an error loading data.
     */
    public EmployeeModuleComponents createEmployeeModule(boolean minimalMode) throws IOException {
        // Initialize repositories
        AuthorisationRepository authorisationRepository = new AuthorisationRepositoryImpl();
        BranchRepository branchRepository = new BranchRepositoryImpl();
        EmployeeRepository employeeRepository = new EmployeeRepositoryImpl();

        // Initialize controllers with repositories
        AuthorisationController authController = new AuthorisationController(
                authorisationRepository);

        EmployeeController employeeController = new EmployeeController(
                employeeRepository,
                branchRepository,
                authorisationRepository,
                authController);

        ShiftReposetory shiftReposetory = new ShiftRepositoryImpl();
        // Initialize ShiftController with the AuthorisationController and
        // EmployeeController
        // The ShiftController will create its own ShiftRepository internally
        ShiftController shiftController = new ShiftController(employeeController, shiftReposetory);

        AssignmentController assignmentController = new AssignmentController(employeeController, shiftController,
                shiftReposetory);
        AvailabilityController availabilityController = new AvailabilityController(employeeController, shiftController,
                shiftReposetory);

        EmployeeService employeeService = new EmployeeService(employeeController, authController);
        ShiftService shiftService = new ShiftService(shiftController, assignmentController, availabilityController);

        return new EmployeeModuleComponents(
                authController,
                employeeController,
                shiftController,
                assignmentController,
                availabilityController,
                employeeService,
                shiftService);
    }

    /**
     * Creates and initializes the Transport Module components.
     * 
     * @param employeeComponents The EmployeeModuleComponents to use for
     *                           integration.
     * @return TransportModuleComponents containing all initialized components.
     */
    public TransportModuleComponents createTransportModule(EmployeeModuleComponents employeeComponents,
            boolean minimalMode) throws SQLException {
        // Initialize facades
        TruckFacade truckFacade = new TruckFacade(); // repository initialized inside here
        SiteFacade siteFacade = new SiteFacade(); // repository initialized inside here

        ObjectMapper objMapper = new ObjectMapper();
        // Set up the ObjectMapper with JavaTimeModule to handle LocalDate and other
        // Java 8 date types.
        objMapper.registerModule(new JavaTimeModule());
        // Optional: Configure SerializationFeature to avoid exceptions when serializing
        // dates to JSON
        objMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // if needed
        // for LocalDateTime serialization

        // Initialize controllers
        TransportController transportController = new TransportController(siteFacade, truckFacade, objMapper); // repositories
                                                                                                               // initialized
                                                                                                               // inside
                                                                                                               // here

        // Create EmployeeIntegrationService
        EmployeeIntegrationService employeeIntegrationService = new EmployeeIntegrationService(
                employeeComponents.getEmployeeService(),
                employeeComponents.getShiftService());

        // Initialize services
        TruckService truckService = new TruckService(truckFacade, employeeIntegrationService);
        SiteService siteService = new SiteService(siteFacade, employeeIntegrationService);
        TransportService transportService = new TransportService(transportController, employeeIntegrationService,
                objMapper);

        // Initialize startup service
        StartUpStateService startUpService = new StartUpStateService(transportService, truckService, siteService); // if
                                                                                                                   // this
                                                                                                                   // specific
                                                                                                                   // one
                                                                                                                   // needed

        return new TransportModuleComponents(
                truckFacade,
                siteFacade,
                transportController,
                truckService,
                transportService,
                siteService,
                startUpService,
                employeeIntegrationService,
                objMapper);
    }

    /**
     * Creates a MainCLI for the Employee Module.
     * 
     * @param employeeService The EmployeeService to use.
     * @param shiftService    The ShiftService to use.
     * @param loginId         The ID of the logged-in employee.
     * @return A MainCLI instance.
     */
    public HR_MainCLI createEmployeeCLI(EmployeeService employeeService, ShiftService shiftService, long loginId) {
        return new HR_MainCLI(employeeService, shiftService, loginId);
    }

    /**
     * Creates a MainTranSysCLI for the Transport Module.
     * 
     * @param ts              The TruckService to use.
     * @param trs             The TransportService to use.
     * @param sis             The SiteService to use.
     * @param starUpStService The StartupService to use.
     * @param es              The EmployeeIntegrationService to use.
     * @return A TranManCLI instance.
     */
    public MainTranSysCLI createTransportCLI(TruckService ts, TransportService trs, SiteService sis,
            StartUpStateService starUpStService, EmployeeIntegrationService es, ObjectMapper oM) {
        // Create & return MainTranSysCLI
        return new MainTranSysCLI(ts, trs, sis, starUpStService, es, oM);
    }

    /**
     * Container class for Employee Module components.
     */
    public static class EmployeeModuleComponents {
        private final AuthorisationController authController;
        private final EmployeeController employeeController;
        private final ShiftController shiftController;
        private final AssignmentController assignmentController;
        private final AvailabilityController availabilityController;
        private final EmployeeService employeeService;
        private final ShiftService shiftService;

        public EmployeeModuleComponents(
                AuthorisationController authController,
                EmployeeController employeeController,
                ShiftController shiftController,
                AssignmentController assignmentController,
                AvailabilityController availabilityController,
                EmployeeService employeeService,
                ShiftService shiftService) {
            this.authController = authController;
            this.employeeController = employeeController;
            this.shiftController = shiftController;
            this.assignmentController = assignmentController;
            this.availabilityController = availabilityController;
            this.employeeService = employeeService;
            this.shiftService = shiftService;
        }

        public AuthorisationController getAuthController() {
            return authController;
        }

        public EmployeeController getEmployeeController() {
            return employeeController;
        }

        public ShiftController getShiftController() {
            return shiftController;
        }

        public AssignmentController getAssignmentController() {
            return assignmentController;
        }

        public AvailabilityController getAvailabilityController() {
            return availabilityController;
        }

        public EmployeeService getEmployeeService() {
            return employeeService;
        }

        public ShiftService getShiftService() {
            return shiftService;
        }
    }

    /**
     * Container class for Transport Module components.
     */
    public static class TransportModuleComponents {
        private final TruckFacade truckFacade;
        private final SiteFacade siteFacade;
        private final TransportController transportController;
        private final TruckService truckService;
        private final TransportService transportService;
        private final SiteService siteService;
        private final StartUpStateService startUpService;
        private final EmployeeIntegrationService employeeIntegrationService;
        private final ObjectMapper oM;

        public TransportModuleComponents(
                TruckFacade truckFacade,
                SiteFacade siteFacade,
                TransportController transportController,
                TruckService truckService,
                TransportService transportService,
                SiteService siteService,
                StartUpStateService startUpService,
                EmployeeIntegrationService employeeIntegrationService,
                ObjectMapper om) {
            this.truckFacade = truckFacade;
            this.siteFacade = siteFacade;
            this.transportController = transportController;
            this.truckService = truckService;
            this.transportService = transportService;
            this.siteService = siteService;
            this.startUpService = startUpService;
            this.employeeIntegrationService = employeeIntegrationService;
            this.oM = om;
        }

        public TruckFacade getTruckFacade() {
            return truckFacade;
        }

        public SiteFacade getSiteFacade() {
            return siteFacade;
        }

        public TransportController getTransportController() {
            return transportController;
        }

        public TruckService getTruckService() {
            return truckService;
        }

        public TransportService getTransportService() {
            return transportService;
        }

        public SiteService getSiteService() {
            return siteService;
        }

        public StartUpStateService getStartUpService() {
            return startUpService;
        }

        public EmployeeIntegrationService getEmployeeIntegrationService() {
            return employeeIntegrationService;
        }

        public ObjectMapper getoM() {
            return oM;
        }
    }

    private SupplierModuleComponents supplierModuleComponents;

    /**
     * Container class for Supplier Module components.
     */
    public static class SupplierModuleComponents {
        private static SupplierModuleComponents supplierModuleComponents;
        private final SupplierService supplierService;
        private final OrderService orderService;
        private final AgreementService agreementService;

        public static void setSupplierModuleComponents(SupplierService supplierService,
                OrderService orderService, AgreementService agreementService) {
            if (supplierModuleComponents == null) {
                supplierModuleComponents = new SupplierModuleComponents(supplierService, orderService,
                        agreementService);
            }
        }

        private SupplierModuleComponents(SupplierService supplierService, OrderService orderService,
                AgreementService agreementService) {
            this.supplierService = supplierService;
            this.orderService = orderService;
            this.agreementService = agreementService;
        }

        public static SupplierModuleComponents getSupplierModuleComponents() {
            return supplierModuleComponents;
        }

        public SupplierService getSupplierService() {
            return supplierService;
        }

        public OrderService getOrderService() {
            return orderService;
        }

        public AgreementService getAgreementService() {
            return agreementService;
        }
    }

    /**
     * Creates and initializes the Supplier Module components.
     * 
     * @return SupplierModuleComponents containing all initialized components.
     */
    private SupplierModuleComponents createSupplierModule() {
        SupplierFacade supplierFacade = new SupplierFacade(InitializeState.CURRENT_STATE);
        OrderFacade orderFacade = new OrderFacade(InitializeState.CURRENT_STATE, supplierFacade);
        SupplierService supplierService = new SupplierService(supplierFacade);
        OrderService orderService = new OrderService(orderFacade);
        AgreementService agreementService = new AgreementService(supplierFacade);
        SupplierModuleComponents.setSupplierModuleComponents(supplierService, orderService, agreementService);
        return SupplierModuleComponents.getSupplierModuleComponents();
    }

    public SupplierModuleComponents getSupplierModule() {
        if (supplierModuleComponents == null) {
            supplierModuleComponents = createSupplierModule();
        }
        return supplierModuleComponents;
    }
}

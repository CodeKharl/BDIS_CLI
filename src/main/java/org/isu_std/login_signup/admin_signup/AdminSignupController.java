package org.isu_std.login_signup.admin_signup;

import org.isu_std.io.Util;
import org.isu_std.io.custom_exception.OperationFailedException;
import org.isu_std.models.model_builders.AdminBuilder;

public class AdminSignupController {
    private final String[] INFORMATION_ATTRIBUTES = {
            "Admin Name (Min. %d)".formatted(AdminSignupService.getMinNameLength()),
            "Pin (Min. %d)".formatted(AdminSignupService.getMinPinLength())
    };

    private final AdminSignupService adminSignupService;
    private final AdminBuilder adminBuilder;

    public AdminSignupController(AdminSignupService adminSignupService){
        this.adminSignupService = adminSignupService;
        this.adminBuilder = adminSignupService.createAdminBuilder();
    }

    protected boolean isInputAccepted(String input, int count){
        try {
            switch (count) {
                case 0 -> {
                    adminSignupService.checkAdminName(input);
                    adminSignupService.checkAdminIdExist(input);
                    adminBuilder.adminName(input);
                }

                case 1 -> {
                    adminSignupService.checkAdminPin(input);
                    adminBuilder.adminPin(Integer.parseInt(input));
                }
            }

            return true;
        }catch (IllegalArgumentException e){
            Util.printException(e.getMessage());
        }

        return false;
    }

    protected boolean adminSignupProcessComplete(){
        try {
            adminSignupService.insertingAdminData(adminBuilder.build());
            Util.printMessage("You successfully created your admin account!");
            Util.printMessage(
                    "Your ADMIN ID : " + adminSignupService.getAdminId(adminBuilder.getAdminName())
            );

            return true;
        }catch (OperationFailedException e){
            Util.printException(e.getMessage());
        }

        return false;
    }

    protected String[] getInfoAttributes(){
        return this.INFORMATION_ATTRIBUTES;
    }
}

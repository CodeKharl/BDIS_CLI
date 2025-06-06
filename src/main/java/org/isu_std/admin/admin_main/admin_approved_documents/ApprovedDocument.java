package org.isu_std.admin.admin_main.admin_approved_documents;

import org.isu_std.admin.admin_main.AdminSection;
import org.isu_std.io.SystemInput;
import org.isu_std.io.Util;

public class ApprovedDocument implements AdminSection {
    private final String[] APPROVE_SECTION_CONTENTS = {
            "Confirm and Export the File", "Open Approved Document File", "View Payment Method",
            "Display Document Details", "Display User Details", "Open Requirement Files",
            "Return to Approve Requests Selection"
    };

    private final ApprovedDocumentController approvedDocumentController;

    protected ApprovedDocument(ApprovedDocumentController approvedDocumentController){
        this.approvedDocumentController = approvedDocumentController;
    }

    @Override
    public void run(String sectionTitle){
        while(true) {
            Util.printSectionTitle(sectionTitle);
            if (!approvedDocumentController.isThereExistingApprovedRequests()) {
                return;
            }

            if(!setApprovedDocumentChoice()){
                return;
            }

            docApprovedValidationProcess();
        }
    }

    private boolean setApprovedDocumentChoice(){
        int backValue = approvedDocumentController.getApprovedDocsCount() + 1;

        approvedDocumentController.printApprovedDocuments();
        Util.printChoice("%d. Back to Admin Menu".formatted(backValue));

        while(true){
            int docsChoice = SystemInput.getIntChoice("Enter approved document choice : ", backValue);

            if(docsChoice == backValue){
                return false;
            }

            if(approvedDocumentController.isDocumentRequestSet(docsChoice)){
                return true;
            }
        }
    }

    private void docApprovedValidationProcess(){
        while(true){
            Util.printSectionTitle(approvedDocumentController.getApprovedSectionTitle());
            Util.printChoices(APPROVE_SECTION_CONTENTS);

            int choice = SystemInput.getIntChoice(
                    "Enter your choice : ",
                    APPROVE_SECTION_CONTENTS.length
            );

            if(choice == APPROVE_SECTION_CONTENTS.length){
                return;
            }

            if(approvedDocumentController.isApprovedValidatingFinished(choice)){
                return;
            }
        }
    }
}

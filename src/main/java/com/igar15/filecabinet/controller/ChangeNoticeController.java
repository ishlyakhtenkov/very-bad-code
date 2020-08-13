package com.igar15.filecabinet.controller;

import com.igar15.filecabinet.entity.ChangeNotice;
import com.igar15.filecabinet.entity.Document;
import com.igar15.filecabinet.service.ChangeNoticeService;
import com.igar15.filecabinet.service.DepartmentService;
import com.igar15.filecabinet.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/changenotices")
public class ChangeNoticeController {

    @Autowired
    private ChangeNoticeService changeNoticeService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public String showAll(@RequestParam(name = "name", required = false) String name,
                          @RequestParam(name = "department", required = false) String department,
                          @RequestParam(name = "changeCode", required = false) String changeCode,
                          @RequestParam(name = "after", required = false) String after,
                          @RequestParam(name = "before", required = false) String before,
                          @SortDefault(value = "issueDate", direction = Sort.Direction.DESC) Pageable pageable,
                          Model model) {

        model.addAttribute("departments", departmentService.findAllByIsDeveloper(true));
        model.addAttribute("department", department);

        Page<ChangeNotice> changeNotices = changeNoticeService.findAll(name, department, changeCode, after, before, pageable);

        model.addAttribute("changeNotices", changeNotices);
        return "/changenotices/changenotice-list";
    }

    @GetMapping("/showAddForm")
    public String showAddForm(Model model) {
        model.addAttribute("changeNotice", new ChangeNotice());
        model.addAttribute("departments", departmentService.findAllByIsDeveloper(true));
        return "/changenotices/changenotice-form";
    }

    @GetMapping("/showFormForUpdate/{id}")
    public String showFormForUpdate(@PathVariable("id") int id, Model model) {
        model.addAttribute("changeNotice", changeNoticeService.findById(id));
        model.addAttribute("departments", departmentService.findAllByIsDeveloper(true));
        return "/changenotices/changenotice-form";
    }

    @PostMapping("/save")
    public String save(@Validated ChangeNotice changeNotice, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAllByIsDeveloper(true));
            return "/changenotices/changenotice-form";
        }
        if (changeNotice.isNew()) {
            model.addAttribute("changeNotice", changeNotice);
            return "/changenotices/changenotice-document-list";
        }
        else {
            changeNoticeService.updateWithoutChildren(changeNotice);
            model.addAttribute("changeNotice", changeNotice);
            return "/changenotices/changenotice-info";
        }
    }

    @GetMapping("/showChangeNoticeInfo/{id}")
    public String showChangeNoticeInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("changeNotice", changeNoticeService.findById(id));
        return "/changenotices/changenotice-info";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        changeNoticeService.deleteById(id);
        return "redirect:/changenotices/list";
    }

    @GetMapping("/showDocuments/{id}")
    public String showDocuments(@PathVariable("id") int id, Model model) {
        model.addAttribute("changeNotice", changeNoticeService.findByIdWithDocuments(id));
        return "/changenotices/changenotice-document-list";
    }

    @PostMapping("/addDocument")
    public String addDocument(@RequestParam(value = "newDocument") String newDocument,
                                @RequestParam(value = "newDocumentChangeNumber") String newDocumentChangeNumber,
                                ChangeNotice changeNotice,
                                Model model) {
        if (!changeNotice.isNew()) {
            changeNotice = changeNoticeService.findByIdWithDocuments(changeNotice.getId());
        }
        Object[] results = changeNoticeService.addDocument(changeNotice, newDocument, newDocumentChangeNumber);
        String docErrorMessage = (String) results[0];
        String numberErrorMessage = (String) results[1];
        model.addAttribute("changeNotice", changeNotice);
        model.addAttribute("docErrorMessage", docErrorMessage);
        model.addAttribute("numberErrorMessage", numberErrorMessage);
        return "/changenotices/changenotice-document-list";
    }

    @GetMapping("/removeDoc/{id}/{documentId}")
    public String removeDoc(@PathVariable("id") int id, @PathVariable("documentId") int documentId, Model model) {
        ChangeNotice changeNotice = changeNoticeService.findByIdWithDocuments(id);
        if (changeNotice.getDocuments().size() == 1) {
            model.addAttribute("changeNotice", changeNotice);
            String errorMessage = "The change notice can not exist without any documents!";
            model.addAttribute("errorMessage", errorMessage);
            return "/changenotices/changenotice-document-list";
        }
        Document document = changeNotice.getDocuments().keySet().stream()
                .filter(doc -> doc.getId().equals(documentId))
                .findFirst().orElse(null);
        changeNotice.getDocuments().remove(document);
        changeNoticeService.update(changeNotice);
        model.addAttribute("changeNotice", changeNotice);
        return "/changenotices/changenotice-document-list";
    }

}

package com.pastebin.pastebin_clone.controller;

import com.pastebin.pastebin_clone.dto.PasteRequest;
import com.pastebin.pastebin_clone.dto.PasteResponse;
import com.pastebin.pastebin_clone.service.PasteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/paste") // <--- Matches the HTML fetch()
@CrossOrigin(origins = "*")        // <--- Allows browser access
public class PasteController {

    @Autowired
    private PasteService pasteService;

    @PostMapping
    public PasteResponse createPaste(@RequestBody PasteRequest request) {
        return pasteService.createPaste(request);
    }

    // Note: We handle the GET request on the same base URL structure
    // If you want to view a paste, you might need to adjust the HTML or this mapping.
    // For now, this handles the creation perfectly.
    @GetMapping("/{urlKey}")
    public String getPaste(@PathVariable String urlKey) {
        return pasteService.getPaste(urlKey);
    }
}
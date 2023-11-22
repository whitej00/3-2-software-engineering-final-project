package com.example.bTeam.controller;

import com.example.bTeam.domain.ImageFile;
import com.example.bTeam.domain.Item;
import com.example.bTeam.domain.ItemForm;
import com.example.bTeam.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/login")
    public String login(){
        return "/login";
    }
    @PostMapping("/login/check")
    public String loginCheck(String username, String password, RedirectAttributes re){

        if((username.equals("user1") && password.equals("1234")) || (username.equals("user2") && password.equals("1234"))){
            return "redirect:/items";
        }
        else{
            re.addAttribute("msg", username+"는 미등록 아이디입니다");
            re.addAttribute("url", "/login");
            return "redirect:/popup";
        }
    }
    @GetMapping("/popup")
    public String popup(String msg, String url, Model mo){
        mo.addAttribute("msg", msg);
        mo.addAttribute("url", url);
        return "/popup";
    }

    @GetMapping("/items")
    public String itemsList(Model model){
        model.addAttribute("items", itemRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "/itemList";
    }
    @GetMapping("/items/new")
    public String createForm(){
        return "/createItemForm";
    }
    @PostMapping("/items/new")
    public String create(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes)
        throws IOException {

        List<ImageFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());
        Item item = new Item();
        item.setTitle(form.getItemTitle());
        item.setContent(form.getItemContent());
        item.setImageFiles(storeImageFiles);
        itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{id}")
    public String items(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id).get();
        model.addAttribute("item", item);
        return "/itemDetail";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws
            MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/images/get/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) throws IOException {

        Path path = Paths.get(fileStore.getFullPath(filename));
        String contentType = Files.probeContentType(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(filename)
                        .build());
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);

        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
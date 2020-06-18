package org.onegang.access.web;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.onegang.access.ActionDeniedException;
import org.onegang.access.ValidationException;
import org.onegang.access.dto.Action;
import org.onegang.access.entity.ApprovalUser;
import org.onegang.access.entity.Request;
import org.onegang.access.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.net.HttpHeaders;

@RestController
@RequestMapping("/api/request")
public class RequestController {

	@Autowired
	private RequestService requestService;
	
	@GetMapping("/filter/{filter}")
	public Collection<Request> getRequests(@PathVariable String filter) {
		return requestService.getRequests(filter);
	}
	
	@GetMapping("/{id}")
	public Request getRequest(@PathVariable int id) {
		return requestService.getRequest(id);
	}
	
	@GetMapping("/{id}/actions")
	public Collection<Action> getRequestActions(@PathVariable int id) {
		return requestService.getRequestActions(id);
	}
	
	@PostMapping("/{id}/action/{action}")
	public Request doAction(@PathVariable int id, @PathVariable Action action) throws ActionDeniedException {
		return requestService.doAction(id, action);
	}
	
	@PostMapping("/approvers")
	public Collection<ApprovalUser> getApprovers(@RequestBody Request request) throws ValidationException {
		return requestService.getApprovers(request);
	}
	
	@PostMapping
	public Request submitRequest(@RequestBody Request request) throws ValidationException {
		return requestService.submitRequest(request);
	}
	
	@PostMapping("/{id}/attachments")
	public String uploadAttachments(@PathVariable int id, @RequestParam("files") MultipartFile[] files) throws IOException {
		for(MultipartFile file: files) {
			requestService.uploadAttachment(id, file.getOriginalFilename(), file.getInputStream());
		}
		return null;
	}
	
	@GetMapping("/{id}/attachments/{filename:.+}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable int id,  
    		@PathVariable String filename, HttpServletRequest request) throws IOException {
        // Load file as Resource
        Resource resource = requestService.downloadAttachment(id, filename);

        // Try to determine file's content type
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
	
}

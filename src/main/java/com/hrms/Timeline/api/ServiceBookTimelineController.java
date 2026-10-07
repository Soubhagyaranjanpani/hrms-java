package com.hrms.Timeline.api;

//
//import com.hrms.serviceBook.dto.TimelineEventDTO;
//import com.hrms.serviceBook.service.ServiceBookTimelineService;
import com.hrms.Timeline.dto.TimelineEventDTO;
import com.hrms.Timeline.application.ServiceBookTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-books")
@RequiredArgsConstructor
public class ServiceBookTimelineController {

    private final ServiceBookTimelineService timelineService;

    @GetMapping("/employees/{employeeId}/timeline")
    public ResponseEntity<List<TimelineEventDTO>> getTimeline(@PathVariable Long employeeId) {
        return ResponseEntity.ok(timelineService.getTimelineForEmployee(employeeId));
    }
}
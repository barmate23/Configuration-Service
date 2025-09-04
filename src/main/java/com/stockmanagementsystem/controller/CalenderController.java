package com.stockmanagementsystem.controller;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.request.HolidayRequest;
import com.stockmanagementsystem.request.ShiftRequest;
import com.stockmanagementsystem.request.UserShiftRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.service.CalenderService;
import com.stockmanagementsystem.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping({APIConstants.BASE_REQUEST + APIConstants.SERVICENAME + APIConstants.CALENDER_CONTROLLER})
public class CalenderController {

    @Autowired
    CalenderService calenderService;

    @GetMapping({APIConstants.GET_HOLIDAYTYPE})
    public BaseResponse<HolidayTypeResponse> getHolidayType(){
        return calenderService.getHolidayType();
    }

    @GetMapping(APIConstants.GET_HOLIDAY)
    public ResponseEntity<BaseResponse> getAllHoliday(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize){
        return calenderService.getAllHoliday(page,pageSize);
    }

    @PostMapping(APIConstants.SAVE_HOLIDAY)
    public BaseResponse saveHoliday(@RequestBody HolidayRequest holidayRequest){
        return calenderService.saveHoliday(holidayRequest);
    }

    @PutMapping(APIConstants.UPDATE_HOLIDAY)
    public BaseResponse<Holiday> updateHolidayCalender(@RequestBody HolidayRequest holidayRequest,@PathVariable Integer holidayId){
        return calenderService.updateByHolidayId(holidayRequest,holidayId);
    }

    @DeleteMapping(APIConstants.DELETE_HOLIDAY)
    public BaseResponse<Holiday> deleteHolidayById(@PathVariable Integer holidayId){
      return calenderService.deleteByHolidayId(holidayId);
    }

    @GetMapping({APIConstants.GET_DAY})
    public ResponseEntity<BaseResponse<List<DayResponse>>> getDays() {
        BaseResponse<List<DayResponse>> response = calenderService.getDay();
            return ResponseEntity.ok(response);
       }

    @GetMapping(APIConstants.GET_HOLIDAYS)
    public ResponseEntity<BaseResponse> getAllHoliday(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        return calenderService.getAllHoliday(page, pageSize, month,year);
    }

    @PostMapping(APIConstants.SAVE_SHIFT)
    public BaseResponse saveShift(@RequestBody ShiftRequest shiftRequest){
        return calenderService.saveShift(shiftRequest);
    }


    @PutMapping(APIConstants.UPDATE_SHIFT)
    public ResponseEntity<BaseResponse> updateShift(@PathVariable Integer shiftId, @RequestBody ShiftRequest shiftRequest) {
        BaseResponse response = calenderService.updateShiftById(shiftId, shiftRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(APIConstants.DELETE_SHIFT)
    public ResponseEntity<BaseResponse> deleteShift(@PathVariable int shiftId) {
        BaseResponse response = calenderService.deleteShiftById(shiftId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(APIConstants.GET_SHIFT)
    public ResponseEntity<BaseResponse> getAllShift(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseResponse response = calenderService.getAllShift(page, pageSize);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(APIConstants.GET_BY_SHIFT_ID)
    public ResponseEntity<BaseResponse> getShiftById(@PathVariable("shiftId") Integer shiftId) {
        BaseResponse response = calenderService.getShiftByShiftId(shiftId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(APIConstants.SAVE_USERS_SHIFT)
    public BaseResponse saveUsersShift(@RequestBody UserShiftRequest userShiftMappperRequest){
        return calenderService.saveUsersShift(userShiftMappperRequest);
    }
      @GetMapping(APIConstants.GET_USER)
      public BaseResponse<List<UserResponse>> getUsers() {
          return calenderService.getUser();
      }

    @GetMapping(APIConstants.GET_USERS_BY_SHIFT)
    public ResponseEntity<BaseResponse> getUsersByShiftMapperId(@PathVariable Integer shiftId) {
        BaseResponse response = calenderService.getUsersByShiftId(shiftId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(APIConstants.UPLOAD_USERS)
    public ResponseEntity<BaseResponse> uploadUsersExcel(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("shiftId") Integer shiftId) {
        BaseResponse response = calenderService.saveUsersShift(file, shiftId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping(APIConstants.DELETE_USER_FROM_SHIFT)
    public BaseResponse<UserShiftMapper> deleteUserByShiftIdAndUserId(@PathVariable Integer shiftId,@PathVariable Integer userId){
        return calenderService.deleteUsersByShiftId(shiftId,userId);
    }

    @GetMapping(APIConstants.GET_USERS)
    public ResponseEntity<BaseResponse> getUsersByShiftMapperIdWithPaginations(
            @PathVariable Integer shiftId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BaseResponse users = calenderService.getUsersByShiftMapperIdWithPaginations(shiftId, page, size);

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }
    @GetMapping(APIConstants.GET_SHIFTS)
    public ResponseEntity<BaseResponse> getAllShifts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        BaseResponse response = calenderService.getAllShiftWithYearAndDays(page, pageSize);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping(APIConstants.GET_USER_NOT_IN_SHIFT)
    public BaseResponse<UserResponse> getUsers(@PathVariable Integer shiftId) {
        return calenderService.getUserNotAddedInShift(shiftId);
    }
}

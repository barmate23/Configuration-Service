package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.request.HolidayRequest;
import com.stockmanagementsystem.request.ShiftRequest;
import com.stockmanagementsystem.request.UserShiftRequest;
import com.stockmanagementsystem.request.WeeklyOffRequest;
import com.stockmanagementsystem.response.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CalenderService {
    BaseResponse<HolidayTypeResponse> getHolidayType();

    BaseResponse saveHoliday(HolidayRequest holidayRequest);


    BaseResponse deleteByHolidayId(Integer holidayId);
    BaseResponse<Holiday> updateByHolidayId(HolidayRequest holidayRequest,Integer holidayId);

    BaseResponse<List<DayResponse>> getDay();

    ResponseEntity<BaseResponse> getAllHoliday();

    BaseResponse saveShift(ShiftRequest ShiftRequest);

    BaseResponse updateShiftById(int shiftId, ShiftRequest shiftRequest);

    BaseResponse deleteShiftById(int shiftId);


    BaseResponse<List<ShiftsResponse>> getAllShift(int page, int pageSize);

    BaseResponse getShiftByShiftId(Integer shiftId);

    BaseResponse saveUsersShift(UserShiftRequest userShiftMappperRequest);

    BaseResponse<List<UserResponse>> getUser();



    BaseResponse getUsersByShiftId(Integer shiftId);

    BaseResponse saveUsersShift(MultipartFile file, Integer shiftId);

    BaseResponse deleteUsersByShiftId(Integer shiftId, Integer userId);


    BaseResponse getUsersByShiftMapperIdWithPaginations(Integer shiftId, int page, int size);

    BaseResponse getAllShiftWithYearAndDays(int page, int pageSize);

    BaseResponse<UserResponse> getUserNotAddedInShift(Integer shiftId);

    BaseResponse<WeeklyOffDays> getWeeklyOff();

    BaseResponse<WeeklyOffDays> saveWeeklyOff(WeeklyOffRequest weeklyOffRequest);
}

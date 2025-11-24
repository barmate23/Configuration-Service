package com.stockmanagementsystem.service;

import com.stockmanagementsystem.entity.*;
import com.stockmanagementsystem.exception.AlredyExistException;
import com.stockmanagementsystem.repository.*;
import com.stockmanagementsystem.request.HolidayRequest;
import com.stockmanagementsystem.request.ShiftRequest;
import com.stockmanagementsystem.request.UserShiftRequest;
import com.stockmanagementsystem.request.WeeklyOffRequest;
import com.stockmanagementsystem.response.*;
import com.stockmanagementsystem.utils.ResponseKeyConstant;
import com.stockmanagementsystem.utils.ServiceConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.stockmanagementsystem.utils.GlobalMessages.getResponseMessages;

@Service
@Slf4j
public class CalenderServiceImpl implements CalenderService {
    @Autowired
    LoginUser loginUser;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    HolidayTypeRepository holidayTypeRepository;
    @Autowired
    DayRepository dayRepository;

    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    WeeklyOffDaysRepository weeklyOffDaysRepository;

    @Autowired
    ShiftMapperRepository shiftMapperRepository;

    @Autowired
    UserShiftRepository userShiftRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public BaseResponse<HolidayTypeResponse> getHolidayType() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getHolidayType - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED HOLIDAY TYPE LIST START");
        BaseResponse baseResponse = new BaseResponse();
        try {
            List<HolidayType> holidayTypeList = holidayTypeRepository.findAll();

            List<HolidayTypeResponse> holidayTypeResponses = new ArrayList<>();
            for (HolidayType holidayType : holidayTypeList) {
                HolidayTypeResponse holidayTypeResponse = new HolidayTypeResponse();
                holidayTypeResponse.setHolidayTypeId(holidayType.getId());
                holidayTypeResponse.setHolidayType(holidayType.getHolidayType());
                holidayTypeResponses.add(holidayTypeResponse);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10045S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(holidayTypeResponses);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CalenderServiceImpl - getHolidayType - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10044F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getHolidayType - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getHolidayType - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED HOLIDAY TYPE LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }


    @Override
    public BaseResponse saveHoliday(HolidayRequest holidayRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE HOLIDAY START");
        BaseResponse baseResponse = new BaseResponse();
        try {
            Holiday existingHoliday = holidayRepository.findBySubOrganizationIdAndIsDeletedAndDateOrSubOrganizationIdAndIsDeletedAndHolidayName(loginUser.getSubOrgId(), false, holidayRequest.getDate(), loginUser.getSubOrgId(), false, holidayRequest.getHolidayName());
            // Holiday existingHoliday = holidayRepository.findBySubOrganizationIdAndIsDeletedAndDateOrHolidayName( loginUser.getSubOrgId(),false,holidayRequest.getDate(), holidayRequest.getHolidayName());
            if (existingHoliday != null) {
                LocalDate existingHolidayDate = existingHoliday.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate holidayRequestDate = holidayRequest.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (existingHolidayDate.equals(holidayRequestDate)) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10033E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    baseResponse.setData(new ArrayList<>());
                } else if (existingHoliday.getHolidayName().equals(holidayRequest.getHolidayName())) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10032E);
                    baseResponse.setCode(responseMessage.getCode());
                    baseResponse.setStatus(responseMessage.getStatus());
                    baseResponse.setMessage(responseMessage.getMessage());
                    baseResponse.setLogId(loginUser.getLogId());
                    baseResponse.setData(new ArrayList<>());
                }
                return baseResponse;
            }
            Holiday holiday = new Holiday();
            holiday.setHolidayName(holidayRequest.getHolidayName());
            holiday.setDate(holidayRequest.getDate());
            holiday.setSubOrganizationId(loginUser.getSubOrgId());
            holiday.setOrganizationId(loginUser.getOrgId());
            holiday.setIsDeleted(false);
            holiday.setCreatedBy(loginUser.getUserId());
            holiday.setCreatedOn(new Date());
            holiday.setHolidayType(holidayTypeRepository.findByIsDeletedAndId(false, holidayRequest.getHolidayType()));
            // holiday.setHolidayType(holidayTypeRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(),false,holidayRequest.getHolidayType()));
            Holiday savedHoliday = holidayRepository.save(holiday);
            HolidayResponse holidayResponse = new HolidayResponse();
            holidayResponse.setHolidayId(savedHoliday.getId());
            holidayResponse.setDate(savedHoliday.getDate());
            holidayResponse.setHolidayName(savedHoliday.getHolidayName());
            holidayResponse.setHolidayType(new HolidayTypeResponse(savedHoliday.getHolidayType().getId(), savedHoliday.getHolidayType().getHolidayType()));
            List<Object> responseData = new ArrayList<>();
            responseData.add(holidayResponse);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10046S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(responseData);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CalenderServiceImpl - saveHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10045F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - saveHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE HOLIDAY TYPE LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public ResponseEntity<BaseResponse> getAllHoliday(int page, int pageSize) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET HOLIDAY LIST START");
        BaseResponse baseResponse = new BaseResponse();
        ResponseEntity responseEntity = null;
        try {
            PageRequest pageable = PageRequest.of((page - 1) * 10, pageSize, Sort.by(Sort.Direction.DESC, "holidayId"));
            Page<Holiday> holidayList = holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false, pageable);
            baseResponse.setData(holidayList.getContent());

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10047S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setTotalRecordCount(holidayList.getTotalElements());
            baseResponse.setTotalPageCount(holidayList.getTotalPages());
            responseEntity = ResponseEntity.ok().body(baseResponse);
            log.info("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FAILED TO FETCH HOLIDAY LIST :" + (endTime - startTime), e);
            responseEntity = ResponseEntity.badRequest().body("FAILED TO FETCH HOLIDAY LIST FROM DB");
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED HOLIDAY LIST TIME :" + (endTime - startTime));
        return responseEntity;
    }

    @Override
    public BaseResponse deleteByHolidayId(Integer holidayId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - deleteByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE HOLIDAY START");
        BaseResponse baseResponse = new BaseResponse();
        try {

            //  Holiday holiday=holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(),false,holidayId);
            Holiday holiday = holidayRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, holidayId);
            holiday.setIsDeleted(true);
            holidayRepository.save(holiday);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10048S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(new ArrayList<>());
            log.info("LogId:{} - CalenderServiceImpl - deleteByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10046F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setData(new ArrayList<>());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - deleteByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - deleteByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE HOLIDAY LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<Holiday> updateByHolidayId(HolidayRequest holidayRequest, Integer holidayId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - updateByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE HOLIDAY START");
        BaseResponse baseResponse = new BaseResponse();

        try {
            // Holiday existingHoliday  = holidayRepository.findHolidayByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(),false, holidayId);
            Holiday existingHoliday = holidayRepository.findHolidayBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, holidayId);

            if (existingHoliday == null) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10034E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(new ArrayList<>());
                return baseResponse;
            }
            Holiday holidayWithUpdatedDate = holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndDate(loginUser.getOrgId(), loginUser.getSubOrgId(), false, holidayRequest.getDate());
            if (holidayWithUpdatedDate != null && !holidayWithUpdatedDate.getId().equals(holidayId)) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10035E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(new ArrayList<>());
                return baseResponse;
            }


            Holiday holidayWithUpdatedName = holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndHolidayName(loginUser.getOrgId(), loginUser.getSubOrgId(), false, holidayRequest.getHolidayName());
            if (holidayWithUpdatedName != null && !holidayWithUpdatedName.getId().equals(holidayId)) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10036E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(new ArrayList<>());
                return baseResponse;
            }

            existingHoliday.setHolidayName(holidayRequest.getHolidayName());
            existingHoliday.setDate(holidayRequest.getDate());
            //    existingHoliday.setHolidayType(holidayTypeRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(),false,holidayRequest.getHolidayType()));
            Optional<HolidayType> byId = holidayTypeRepository.findById(holidayRequest.getHolidayType());
            existingHoliday.setHolidayType(byId.get());
          /* existingHoliday.setModifiedBy(loginUser.getUserId());
           existingHoliday.setModifiedOn(new Date());
*/
            holidayRepository.save(existingHoliday);
            if (existingHoliday != null) {
                HolidayResponse holidayResponse = new HolidayResponse();
                holidayResponse.setHolidayId(existingHoliday.getId());
                holidayResponse.setDate(existingHoliday.getDate());
                holidayResponse.setHolidayName(existingHoliday.getHolidayName());
                if (existingHoliday.getHolidayType() != null) {
                    holidayResponse.setHolidayType(new HolidayTypeResponse(existingHoliday.getHolidayType().getId(), existingHoliday.getHolidayType().getHolidayType()));
                }
                List<Object> responseData = new ArrayList<>();
                responseData.add(holidayResponse);
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10049S);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setLogId(loginUser.getLogId());
                baseResponse.setData(responseData);
                log.info("LogId:{} - CalenderServiceImpl - updateByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());

            }
        } catch (Exception e) {
            e.printStackTrace();
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10047F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - updateByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - updateByHolidayId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE HOLIDAY TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<List<DayResponse>> getDay() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getDay - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET DAY LIST START");
        BaseResponse<List<DayResponse>> baseResponse = new BaseResponse<>();
        try {
            List<Day> dayList = dayRepository.findAll();
            List<DayResponse> dayResponseList = new ArrayList<>();
            for (Day day : dayList) {
                DayResponse dayResponse = new DayResponse();
                dayResponse.setDayId(day.getId());
                dayResponse.setDay(day.getDay());
                dayResponseList.add(dayResponse);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10050S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(Collections.singletonList(dayResponseList));
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CalenderServiceImpl - getDay - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10048F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getDay - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getDay - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED DAY LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public ResponseEntity<BaseResponse> getAllHoliday(int page, int pageSize, Integer month, Integer year) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET HOLIDAYS START");
        BaseResponse baseResponse = new BaseResponse();
        ResponseEntity responseEntity = null;

        try {
            PageRequest pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.ASC, "date"));
            Page<Holiday> holidayList;

            if (month != null || year != null) {
                Calendar calendarStart = Calendar.getInstance();
                Calendar calendarEnd = Calendar.getInstance();

                if (year == null) {
                    year = calendarStart.get(Calendar.YEAR);
                }

                calendarStart.set(Calendar.YEAR, year);
                calendarEnd.set(Calendar.YEAR, year);

                if (month != null) {
                    calendarStart.set(Calendar.MONTH, month - 1);
                    calendarEnd.set(Calendar.MONTH, month - 1);
                } else {
                    calendarStart.set(Calendar.MONTH, Calendar.JANUARY);
                    calendarEnd.set(Calendar.MONTH, Calendar.DECEMBER);
                }

                calendarStart.set(Calendar.DAY_OF_MONTH, 1);
                calendarStart.set(Calendar.HOUR_OF_DAY, 0);
                calendarStart.set(Calendar.MINUTE, 0);
                calendarStart.set(Calendar.SECOND, 0);
                Date startDate = calendarStart.getTime();

                calendarEnd.set(Calendar.DAY_OF_MONTH, calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
                calendarEnd.set(Calendar.MINUTE, 59);
                calendarEnd.set(Calendar.SECOND, 59);
                Date endDate = calendarEnd.getTime();


                holidayList = holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndDateBetween(
                        loginUser.getOrgId(), loginUser.getSubOrgId(), false, startDate, endDate, pageable);

            } else {
                holidayList = holidayRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(
                        loginUser.getOrgId(), loginUser.getSubOrgId(), false, pageable);
            }

            List<HolidayResponse> holidayResponses = new ArrayList<>();
            for (Holiday savedHoliday : holidayList.getContent()) {
                HolidayResponse holidayResponse = new HolidayResponse();
                holidayResponse.setHolidayId(savedHoliday.getId());
                holidayResponse.setDate(savedHoliday.getDate());
                holidayResponse.setHolidayName(savedHoliday.getHolidayName());
                if (savedHoliday.getHolidayType() != null) {
                    holidayResponse.setHolidayType(new HolidayTypeResponse(savedHoliday.getHolidayType().getId(), savedHoliday.getHolidayType().getHolidayType()));
                }
                holidayResponses.add(holidayResponse);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10051S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(holidayResponses);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setTotalRecordCount(holidayList.getTotalElements());
            baseResponse.setTotalPageCount(holidayList.getTotalPages());
            responseEntity = ResponseEntity.ok().body(baseResponse);
            log.info("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FAILED TO FETCH HOLIDAY LIST :" + (endTime - startTime), e);
            responseEntity = ResponseEntity.badRequest().body("FAILED TO FETCH HOLIDAY LIST FROM DB");
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllHoliday - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCH HOLIDAY LIST TIME :" + (endTime - startTime));
        return responseEntity;
    }


    @Override
    public BaseResponse saveShift(ShiftRequest shiftRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE SHIFT START");
        BaseResponse response = new BaseResponse();
        try {
            long shiftCount =
                    shiftRepository.countByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false);
            if (shiftCount >= 4) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10037E);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(Collections.emptyList());
                response.setLogId(loginUser.getLogId());
                return response;
            }

            if (shiftRepository.existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShiftName(loginUser.getOrgId(), loginUser.getSubOrgId(), false, shiftRequest.getShiftName())) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10038E);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(Collections.emptyList());
                response.setLogId(loginUser.getLogId());
                return response;
            }


            Shift shift = new Shift();
            shift.setShiftName(shiftRequest.getShiftName());
            shift.setShiftStart(shiftRequest.getShiftStart());
            shift.setShiftEnd(shiftRequest.getShiftEnd());
            shift.setOrganizationId(loginUser.getOrgId());
            shift.setSubOrganizationId(loginUser.getSubOrgId());
            shift.setIsDeleted(false);
            shift.setCreatedBy(loginUser.getUserId());
            shift.setCreatedOn(new Date());

            Shift savedShift = shiftRepository.save(shift);

            Date year = shiftRequest.getYear();

            for (Integer dayId : shiftRequest.getDay()) {
                Day day = dayRepository.findById(dayId)
                        .orElseThrow(() -> new IllegalArgumentException("DAY NOT FOUND"));

                ShiftMapper shiftMapper = new ShiftMapper();
                shiftMapper.setShift(savedShift);
                shiftMapper.setYear(year);
                shiftMapper.setDay(day);
                shiftMapper.setOrganizationId(loginUser.getOrgId());
                shiftMapper.setSubOrganizationId(loginUser.getSubOrgId());
                shiftMapper.setIsDeleted(false);
                shiftMapper.setCreatedBy(loginUser.getUserId());
                shiftMapper.setCreatedOn(new Date());
                shiftMapperRepository.save(shiftMapper);
            }
            List<Object> responseData = new ArrayList<>();
            responseData.add(new ShiftsResponse(savedShift.getId(), savedShift.getShiftName(), savedShift.getShiftStart(), savedShift.getShiftEnd()));

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10052S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(responseData);
            log.info("LogId:{} - CalenderServiceImpl - saveShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10049F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - saveShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCH HOLIDAY LIST TIME :" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse updateShiftById(int shiftId, ShiftRequest shiftRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - updateShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE SHIFT START");
        BaseResponse response = new BaseResponse();
        try {
            Optional<Shift> optionalShift = shiftRepository.findByIdAndSubOrganizationIdAndIsDeleted(shiftId, loginUser.getSubOrgId(), false);
            if (optionalShift.isPresent()) {
                Shift existingShift = optionalShift.get();

                if (shiftRepository.existsByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShiftNameAndIdNot(loginUser.getOrgId(), loginUser.getSubOrgId(), false, shiftRequest.getShiftName(), shiftId)) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10039E);
                    response.setCode(responseMessage.getCode());
                    response.setStatus(responseMessage.getStatus());
                    response.setMessage(responseMessage.getMessage());
                    response.setData(Collections.emptyList());
                    response.setLogId(loginUser.getLogId());
                    return response;
                }

                existingShift.setShiftName(shiftRequest.getShiftName());
                existingShift.setShiftStart(shiftRequest.getShiftStart());
                existingShift.setShiftEnd(shiftRequest.getShiftEnd());

                Shift savedShift = shiftRepository.save(existingShift);

                Date year = shiftRequest.getYear();

                List<ShiftMapper> existingMappings = shiftMapperRepository.findBySubOrganizationIdAndIsDeletedAndShift_Id(loginUser.getSubOrgId(), false, shiftId);

                Set<Integer> updatedDayIds = new HashSet<>(shiftRequest.getDay());

                for (ShiftMapper existingMapper : existingMappings) {
                    int dayId = existingMapper.getDay().getId();

                    if (updatedDayIds.contains(dayId)) {
                        existingMapper.setShift(savedShift);
                        existingMapper.setYear(year);
                        existingMapper.setIsDeleted(false);
                        shiftMapperRepository.save(existingMapper);
                    } else {
                        existingMapper.setIsDeleted(true);
                        shiftMapperRepository.save(existingMapper);
                    }
                    updatedDayIds.remove(dayId);
                }

                for (Integer newDayId : updatedDayIds) {
                    Day newDay = dayRepository.findById(newDayId)
                            .orElseThrow(() -> new IllegalArgumentException("DAY NOT FOUND"));

                    ShiftMapper newMapper = new ShiftMapper();
                    newMapper.setShift(savedShift);
                    newMapper.setYear(year);
                    newMapper.setDay(newDay);
                    newMapper.setOrganizationId(loginUser.getOrgId());
                    newMapper.setSubOrganizationId(loginUser.getSubOrgId());
                    newMapper.setIsDeleted(false);
                    newMapper.setCreatedBy(loginUser.getUserId());
                    newMapper.setCreatedOn(new Date());
                    shiftMapperRepository.save(newMapper);
                }

                List<Object> responseData = new ArrayList<>();
                responseData.add(new ShiftsResponse(savedShift.getId(), savedShift.getShiftName(), savedShift.getShiftStart(), savedShift.getShiftEnd()));
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10053S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setLogId(loginUser.getLogId());
                response.setData(responseData);
                log.info("LogId:{} - CalenderServiceImpl - updateShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
            } else {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10040E);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setLogId(loginUser.getLogId());
            }
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10050F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - updateShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - updateShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPDATE SHIFT  :" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse deleteShiftById(int shiftId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - deleteShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE SHIFT START");
        BaseResponse response = new BaseResponse();
        try {
            Optional<Shift> optionalShift = shiftRepository.findById(shiftId);
            if (optionalShift.isPresent()) {
                Shift shift = optionalShift.get();
                shift.setIsDeleted(true);
                shiftRepository.save(shift);

                List<ShiftMapper> mappings = shiftMapperRepository.findByOrganizationIdAndSubOrganizationIdAndShift(loginUser.getOrgId(), loginUser.getSubOrgId(), shift);
                for (ShiftMapper mapping : mappings) {
                    mapping.setIsDeleted(true);
                }
                shiftMapperRepository.saveAll(mappings);
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10054S);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(new ArrayList<>());
                response.setLogId(loginUser.getLogId());
                log.info("LogId:{} - CalenderServiceImpl - deleteShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());

            } else {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10041E);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setLogId(loginUser.getLogId());
            }
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10051F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - deleteShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - deleteShiftById - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE SHIFT BY SHIFT :" + (endTime - startTime));
        return response;
    }


    @Override
    public BaseResponse getAllShift(int page, int pageSize) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET SHIFTS START");
        BaseResponse baseResponse = new BaseResponse<>();
        try {
            PageRequest pageable = PageRequest.of((page - 1) * pageSize, pageSize, Sort.by(Sort.Direction.DESC, "id"));
            Page<Shift> shiftPage = shiftRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeleted(loginUser.getOrgId(), loginUser.getSubOrgId(), false, pageable);

            List<ShiftsResponse> shiftsResponseList = new ArrayList<>();
            for (Shift shift : shiftPage.getContent()) {
                ShiftsResponse shiftsResponse = new ShiftsResponse();
                shiftsResponse.setShiftId(shift.getId());
                shiftsResponse.setShiftName(shift.getShiftName());
                shiftsResponse.setShiftStart(shift.getShiftStart());
                shiftsResponse.setShiftEnd(shift.getShiftEnd());
                shiftsResponseList.add(shiftsResponse);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10055S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(shiftsResponseList);
            baseResponse.setLogId(loginUser.getLogId());
            baseResponse.setTotalRecordCount(shiftPage.getTotalElements());
            baseResponse.setTotalPageCount(shiftPage.getTotalPages());
            log.info("LogId:{} - CalenderServiceImpl - getAllShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {

            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10052F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getAllShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED SHIFTS TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse getShiftByShiftId(Integer shiftId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getShiftByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET SHIFT BY ID  START");
        BaseResponse response = new BaseResponse();

        try {
            //Shift shift = shiftRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndId(loginUser.getOrgId(), loginUser.getSubOrgId(),false,shiftId);
            Shift shift = shiftRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, shiftId);

            ShiftResponse shiftResponse = new ShiftResponse();
            ShiftsResponse shiftsResponse = new ShiftsResponse();
            shiftsResponse.setShiftId(shift.getId());
            shiftsResponse.setShiftName(shift.getShiftName());
            shiftsResponse.setShiftStart(shift.getShiftStart());
            shiftsResponse.setShiftEnd(shift.getShiftEnd());
            shiftResponse.setShift(shiftsResponse);

            List<ShiftMapper> shiftMappers = shiftMapperRepository.findBySubOrganizationIdAndIsDeletedAndShift(loginUser.getSubOrgId(), false, shift);
            //   List<ShiftMapper> shiftMappers = shiftMapperRepository.findByOrganizationIdAndSubOrganizationIdAndShift(loginUser.getOrgId(), loginUser.getSubOrgId(),shift);
            Set<ShiftMapperResponse> shiftMapperResponses = new HashSet<>();

            for (ShiftMapper shiftMapper : shiftMappers) {
                Date year = shiftMapper.getYear();
                //  List<ShiftMapper> days = shiftMapperRepository.findByOrganizationIdAndSubOrganizationIdAndShiftAndYear(loginUser.getOrgId(), loginUser.getSubOrgId(),shift, year);
                List<ShiftMapper> days = shiftMapperRepository.findBySubOrganizationIdAndIsDeletedAndShiftAndYear(loginUser.getSubOrgId(), false, shift, year);
                for (ShiftMapper day : days) {
                    ShiftMapperResponse shiftMapperResponse = new ShiftMapperResponse();
                    shiftMapperResponse.setDay(new DayResponse(day.getDay().getId(), day.getDay().getDay()));
                    shiftMapperResponses.add(shiftMapperResponse);
                }
                shiftResponse.setYearResponse(shiftMapper.getYear());
            }


            shiftResponse.setShiftMapperResponses(new ArrayList<>(shiftMapperResponses));

            List<Object> responseData = new ArrayList<>();
            responseData.add(shiftResponse);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10056S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(responseData);
            log.info("LogId:{} - CalenderServiceImpl - getShiftByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10053F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getShiftByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getShiftByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET SHIFT BY SHIFT ID TIME :" + (endTime - startTime));
        return response;
    }


    @Override
    public BaseResponse saveUsersShift(UserShiftRequest userShiftRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE USERS TO SHIFT START");
        BaseResponse response = new BaseResponse();
        try {
            List<UserShiftMapper> userShiftMappper = new ArrayList<>();
            Shift shiftId = shiftRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, userShiftRequest.getShiftId());

            for (Integer id : userShiftRequest.getUserId()) {

                Users existingUser = userRepository.findBySubOrganizationIdAndIsDeletedAndIsActiveAndId(loginUser.getSubOrgId(), false, true, id);

                if (existingUser == null) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10042E);
                    response.setCode(responseMessage.getCode());
                    response.setStatus(responseMessage.getStatus());
                    response.setMessage(responseMessage.getMessage());
                    response.setData(new ArrayList<>());
                    response.setLogId(loginUser.getLogId());
                    return response;
                }


                UserShiftMapper userShiftMapppers = new UserShiftMapper();

                boolean isUserAlreadyAssigned = userShiftRepository.existsBySubOrganizationIdAndIsDeletedAndUserAndShift_id(loginUser.getSubOrgId(), false, existingUser, shiftId.getId());

                if (!isUserAlreadyAssigned) {

                    userShiftMapppers.setUser(existingUser);
                    userShiftMapppers.setShift(shiftId);

                    userShiftMapppers.setOrganizationId(loginUser.getOrgId());
                    userShiftMapppers.setSubOrganizationId(loginUser.getSubOrgId());
                    userShiftMapppers.setIsDeleted(false);
                    userShiftMapppers.setCreatedBy(loginUser.getUserId());
                    userShiftMapppers.setCreatedOn(new Date());
                    userShiftMappper.add(userShiftMapppers);

                    userShiftRepository.saveAll(userShiftMappper);

                } else {


                    userShiftMapppers.setUser(existingUser);
                    userShiftMapppers.setShift(shiftId);
                    userShiftMapppers.setOrganizationId(loginUser.getOrgId());
                    userShiftMapppers.setSubOrganizationId(loginUser.getSubOrgId());
                    userShiftMapppers.setIsDeleted(false);
                    userShiftMapppers.setCreatedBy(loginUser.getUserId());
                    userShiftMapppers.setCreatedOn(new Date());

                }
            }
            List<UserShiftMapper> save = userShiftRepository.saveAll(userShiftMappper);

            UserShiftsResponse userShiftMapperResponse = new UserShiftsResponse();
            userShiftMapperResponse.setShift(new ShiftsResponse(shiftId.getId(), shiftId.getShiftName(), shiftId.getShiftStart(), shiftId.getShiftEnd()));
            List<UserShiftResponse> userShiftResponses = new ArrayList<>();
            for (UserShiftMapper userShiftMapper : save) {
                UserShiftResponse userShiftResponse = new UserShiftResponse();
                Users id = userRepository.findBySubOrganizationIdAndIsDeletedAndIsActiveAndId(loginUser.getSubOrgId(), false, true, userShiftMapper.getUser().getId());
                userShiftResponse.setUser(id);
                userShiftResponses.add(userShiftResponse);
            }
            userShiftMapperResponse.setUserShiftResponse(userShiftResponses);
            List<Object> responseData = new ArrayList<>();
            responseData.add(userShiftMapperResponse);
            response.setLogId(loginUser.getLogId());

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10057S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(responseData);
            log.info("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10054F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE USERS SHIFT TIME :" + (endTime - startTime));
        return response;

    }


    @Override
    public BaseResponse<List<UserResponse>> getUser() {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUser - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET USERS START");
        BaseResponse<List<UserResponse>> baseResponse = new BaseResponse<>();
        try {
            List<Users> userList = userRepository.findByIsDeletedAndSubOrganizationId(false, loginUser.getSubOrgId());
            List<UserResponse> userResponseList = new ArrayList<>();
            for (Users user : userList) {
                UserResponse userResponse = new UserResponse();
                userResponse.setId(user.getId());
                userResponse.setUserId(user.getUserId());
                userResponse.setUserName(user.getUsername());
                userResponseList.add(userResponse);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10058S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(Collections.singletonList(userResponseList));
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CalenderServiceImpl - getUser - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10055F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getUser - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUser - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), "  FETCHED USERS LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse getUsersByShiftId(Integer shiftId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET USER BY ID START");
        BaseResponse response = new BaseResponse();
        try {
            List<UserShiftMapper> userShiftMappers = userShiftRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShift_Id(loginUser.getOrgId(), loginUser.getSubOrgId(), false, shiftId);
            Set<UserShiftResponse> responseData = new HashSet<>();
            UserShiftsResponse userShiftMapperResponse = new UserShiftsResponse();

            for (UserShiftMapper userShiftMapper : userShiftMappers) {
                Shift shift = userShiftMapper.getShift();
                List<UserShiftMapper> userList = userShiftRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndUser(loginUser.getOrgId(), loginUser.getSubOrgId(), false, userShiftMapper.getUser());
                UserShiftResponse userShiftResponse = new UserShiftResponse();
                userShiftMapperResponse.setShift(new ShiftsResponse(shift.getId(), shift.getShiftName(), shift.getShiftStart(), shift.getShiftEnd()));
                for (UserShiftMapper user : userList) {
                    userShiftResponse.setUser(user.getUser());
                    responseData.add(userShiftResponse);
                }
                userShiftMapperResponse.setUserShiftResponse(new ArrayList<>(responseData));
            }

            List<Object> responseDataList = new ArrayList<>();
            responseDataList.add(userShiftMapperResponse);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10059S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(responseDataList);
            log.info("LogId:{} - CalenderServiceImpl - getUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10056F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED USERS BY SHIFT ID TIME :" + (endTime - startTime));
        return response;
    }


    @Override
    public BaseResponse saveUsersShift(MultipartFile file, Integer shiftId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " UPLOAD USERS START");
        BaseResponse response = new BaseResponse();
        try {
            Shift shift = shiftRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, shiftId);
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();

            // Check if there's at least one row after skipping headers
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip first header row
            }
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip second header row
            }

            boolean hasData = false;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell cell = row.getCell(0);

                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    hasData = true;
                    break;
                }
            }

            if (!hasData) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10066E);
                response.setCode(responseMessage.getCode());
                response.setStatus(responseMessage.getStatus());
                response.setMessage(responseMessage.getMessage());
                response.setData(new ArrayList<>());
                response.setLogId(loginUser.getLogId());
                return response;
            }


            //Iterator<Row> rowIterator = sheet.iterator();
            rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            List<UserShiftMapper> userShiftMappers = new ArrayList<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                UserShiftMapper userShiftMapper = new UserShiftMapper();
                Users user = new Users();

                Cell cell = row.getCell(0); // Assuming the user ID is in the first column

                if (cell == null || cell.getCellType() == CellType.BLANK) {
                    continue;
                }
                user.setUserId(getStringCellValue(cell));

                Users existingUser = userRepository.findBySubOrganizationIdAndIsDeletedAndIsActiveAndUserId(loginUser.getSubOrgId(), false, true, user.getUserId());

                if (existingUser == null) {
                    ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10043E);
                    response.setCode(responseMessage.getCode());
                    response.setStatus(responseMessage.getStatus());
                    response.setMessage(responseMessage.getMessage());
                    response.setData(new ArrayList<>());
                    response.setLogId(loginUser.getLogId());
                    return response;
                }


                boolean isUserAlreadyAssigned = userShiftRepository.existsBySubOrganizationIdAndIsDeletedAndUserAndShift(loginUser.getSubOrgId(), false, existingUser, shift);

                if (!isUserAlreadyAssigned) {

                    userShiftMapper.setUser(existingUser);
                    userShiftMapper.setShift(shift);
                    userShiftMapper.setIsDeleted(false);
                    userShiftMapper.setCreatedBy(loginUser.getUserId());
                    userShiftMapper.setCreatedOn(new Date());

                    userShiftMapper.setOrganizationId(loginUser.getOrgId());
                    userShiftMapper.setSubOrganizationId(loginUser.getSubOrgId());

                    userShiftMappers.add(userShiftMapper);
                    userShiftRepository.saveAll(userShiftMappers);

                } else {
                    userShiftMapper.setUser(existingUser);
                    userShiftMapper.setShift(shift);
                    userShiftMapper.setIsDeleted(false);
                    userShiftMapper.setCreatedBy(loginUser.getUserId());
                    userShiftMapper.setCreatedOn(new Date());

                    userShiftMapper.setOrganizationId(loginUser.getOrgId());
                    userShiftMapper.setSubOrganizationId(loginUser.getSubOrgId());
                }

            }

            List<UserShiftMapper> savedUserShiftMappers = userShiftRepository.saveAll(userShiftMappers);


            UserShiftsResponse userShiftMapperResponse = new UserShiftsResponse();
            userShiftMapperResponse.setShift(new ShiftsResponse(shift.getId(), shift.getShiftName(), shift.getShiftStart(), shift.getShiftEnd()));
            List<UserShiftResponse> userShiftResponses = new ArrayList<>();
            for (UserShiftMapper userShiftMapper : savedUserShiftMappers) {
                UserShiftResponse userShiftResponse = new UserShiftResponse();
                Users id = userRepository.findBySubOrganizationIdAndIsDeletedAndIsActiveAndId(loginUser.getSubOrgId(), false, true, userShiftMapper.getUser().getId());
                userShiftResponse.setUser(id);
                userShiftResponses.add(userShiftResponse);
            }
            userShiftMapperResponse.setUserShiftResponse(userShiftResponses);

            List<Object> responseData = new ArrayList<>();
            responseData.add(userShiftMapperResponse);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10060S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(responseData);
            log.info("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10057F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - saveUsersShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " SAVE USERS SHIFT TIME :" + (endTime - startTime));
        return response;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    @Override
    public BaseResponse deleteUsersByShiftId(Integer shiftId, Integer userId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - deleteUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE USERS START");
        BaseResponse response = new BaseResponse();
        try {
            Optional<UserShiftMapper> userShiftMappers = userShiftRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShift_IdAndUser_Id(loginUser.getOrgId(), loginUser.getSubOrgId(), false, shiftId, userId);
            userShiftMappers.get().setIsDeleted(true);
            userShiftRepository.save(userShiftMappers.get());
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10061S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(new ArrayList<>());
            log.info("LogId:{} - CalenderServiceImpl - deleteUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10058F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(new ArrayList<>());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - deleteUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - deleteUsersByShiftId - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " DELETE USERS BY SHIFT ID TIME :" + (endTime - startTime));
        return response;
    }


    @Override
    public BaseResponse getUsersByShiftMapperIdWithPaginations(Integer shiftId, int page, int size) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUsersByShiftMapperIdWithPaginations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET USERS BY SHIFTMAPPER ID START");
        BaseResponse response = new BaseResponse();

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserShiftMapper> userShiftMapperPage = userShiftRepository.findByOrganizationIdAndSubOrganizationIdAndIsDeletedAndShift_Id(loginUser.getOrgId(), loginUser.getSubOrgId(), false, shiftId, pageable);

            List<UserShiftMapper> userShiftMappers = userShiftMapperPage.getContent();
            List<UserShiftResponse> userShiftResponses = new ArrayList<>();

            for (UserShiftMapper userShiftMapper : userShiftMappers) {
                UserShiftResponse userShiftResponse = new UserShiftResponse();
                userShiftResponse.setUser(userShiftMapper.getUser());
                userShiftResponses.add(userShiftResponse);
            }
            UserShiftsResponse userShiftMapperResponse = new UserShiftsResponse();
            userShiftMapperResponse.setShift(new ShiftsResponse(userShiftMapperPage.getContent().get(0).getShift().getId(), userShiftMapperPage.getContent().get(0).getShift().getShiftName(), userShiftMapperPage.getContent().get(0).getShift().getShiftStart(), userShiftMapperPage.getContent().get(0).getShift().getShiftEnd()));
            userShiftMapperResponse.setUserShiftResponse(userShiftResponses
            );
            List<Object> responseData = new ArrayList<>();
            responseData.add(userShiftMapperResponse);
            response.setTotalPageCount(userShiftMapperPage.getTotalPages());
            response.setTotalRecordCount(userShiftMapperPage.getTotalElements());

            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10062S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(responseData);
            log.info("LogId:{} - CalenderServiceImpl - getUsersByShiftMapperIdWithPaginations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10059F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setData(new ArrayList<>());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getUsersByShiftMapperIdWithPaginations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUsersByShiftMapperIdWithPaginations - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED USERS BY SHIFTMAPPER ID LIST TIME :" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse getAllShiftWithYearAndDays(int page, int pageSize) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllShiftWithYearAndDays - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET ALL SHIFTS START");
        BaseResponse response = new BaseResponse();

        try {
            PageRequest pageable = PageRequest.of((page - 1) * pageSize, pageSize, Sort.by(Sort.Direction.DESC, "id"));
            Page<Shift> shiftPage = shiftRepository.findAllBySubOrganizationIdAndIsDeleted(loginUser.getSubOrgId(), false, pageable);
            List<ShiftResponse> shiftResponses = new ArrayList<>();
            List<Shift> shifts = shiftPage.getContent();
            for (Shift shift : shifts) {

                ShiftResponse shiftResponseList = new ShiftResponse();
                ShiftsResponse shiftResponse = new ShiftsResponse();
                shiftResponse.setShiftId(shift.getId());
                shiftResponse.setShiftName(shift.getShiftName());
                shiftResponse.setShiftStart(shift.getShiftStart());
                shiftResponse.setShiftEnd(shift.getShiftEnd());
                shiftResponseList.setShift(shiftResponse);
                // Fetch shift mappers for the current shift
                //  List<ShiftMapper> shiftMappers = shiftMapperRepository.findByOrganizationIdAndSubOrganizationIdAndShift(loginUser.getOrgId(), loginUser.getSubOrgId(), shift);
                List<ShiftMapper> shiftMappers = shiftMapperRepository.findBySubOrganizationIdAndIsDeletedAndShift(loginUser.getSubOrgId(), false, shift);
                Set<ShiftMapperResponse> shiftMapperResponses = new HashSet<>();

                for (ShiftMapper shiftMapper : shiftMappers) {
                    Date year = shiftMapper.getYear();
                    List<ShiftMapper> days = shiftMapperRepository.findBySubOrganizationIdAndIsDeletedAndShiftAndYear(loginUser.getSubOrgId(), false, shift, year);
                    // List<ShiftMapper> days = shiftMapperRepository.findByOrganizationIdAndSubOrganizationIdAndShiftAndYear(loginUser.getOrgId(), loginUser.getSubOrgId(), shift, year);
                    for (ShiftMapper day : days) {
                        ShiftMapperResponse shiftMapperResponse = new ShiftMapperResponse();
                        shiftMapperResponse.setDay(new DayResponse(day.getDay().getId(), day.getDay().getDay()));
                        shiftMapperResponses.add(shiftMapperResponse);
                    }
                    shiftResponseList.setYearResponse(shiftMapper.getYear());
                }

                shiftResponseList.setShiftMapperResponses(new ArrayList<>(shiftMapperResponses));
                shiftResponses.add(shiftResponseList);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10063S);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());
            response.setLogId(loginUser.getLogId());
            response.setTotalPageCount(shiftPage.getTotalPages());
            response.setTotalRecordCount(shiftPage.getTotalElements());
            response.setData(shiftResponses);
            log.info("LogId:{} - CalenderServiceImpl - getAllShiftWithYearAndDays - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10060F);
            response.setCode(responseMessage.getCode());
            response.setStatus(responseMessage.getStatus());
            response.setMessage(responseMessage.getMessage());

            e.printStackTrace();
            response.setData(new ArrayList<>());
            response.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getAllShiftWithYearAndDays - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getAllShiftWithYearAndDays - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCH ALL SHIFTS TIME :" + (endTime - startTime));
        return response;
    }

    @Override
    public BaseResponse<UserResponse> getUserNotAddedInShift(Integer shiftId) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET USERS START");
        BaseResponse<UserResponse> baseResponse = new BaseResponse<>();
        try {
            Shift shift = shiftRepository.findBySubOrganizationIdAndIsDeletedAndId(loginUser.getSubOrgId(), false, shiftId);

            // Check if shift ID is not found
            if (shift == null) {
                ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10044E);
                baseResponse.setCode(responseMessage.getCode());
                baseResponse.setStatus(responseMessage.getStatus());
                baseResponse.setMessage(responseMessage.getMessage());
                baseResponse.setData(new ArrayList<>());
                baseResponse.setLogId(loginUser.getLogId());
                return baseResponse;
            }

            List<Users> userList = userRepository.findUsersNotAssignedToShift(loginUser.getSubOrgId(), false, shiftId);
            List<UserResponse> userResponseList = new ArrayList<>();
            for (Users user : userList) {
                UserResponse userResponse = new UserResponse();
                userResponse.setId(user.getId());
                userResponse.setUserId(user.getUserId());
                userResponse.setUserName(user.getUsername());
                userResponseList.add(userResponse);
            }
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10064S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(userResponseList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10061F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED USERS LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }

    @Override
    public BaseResponse<WeeklyOffDays> getWeeklyOff() {

        long startTime = System.currentTimeMillis();
        String logId = loginUser.getLogId();
        Integer userId = loginUser.getUserId();

        log.info("LogId:{} - CalendarServiceImpl - getWeeklyOff - UserId:{} - START", logId, userId);

        BaseResponse<WeeklyOffDays> baseResponse = new BaseResponse<>();

        try {

            List<WeeklyOffDays> weeklyOffDaysList = weeklyOffDaysRepository.findByIsChecked(true);

            // 🔹 HARD–CODED SUCCESS MESSAGE
            baseResponse.setCode(200);
            baseResponse.setStatus(1);
            baseResponse.setMessage("Weekly off days fetched successfully.");
            baseResponse.setData(weeklyOffDaysList);
            baseResponse.setLogId(logId);

            log.info("LogId:{} - CalendarServiceImpl - getWeeklyOff - UserId:{} - SUCCESS - Weekly off days fetched successfully.",
                    logId, userId);

        } catch (Exception e) {

            // 🔹 HARD–CODED FAILURE MESSAGE
            baseResponse.setCode(500);
            baseResponse.setStatus(0);
            baseResponse.setMessage("Failed to fetch weekly off days.");
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(logId);

            log.error("LogId:{} - CalendarServiceImpl - getWeeklyOff - UserId:{} - ERROR - Failed to fetch weekly off days.",
                    logId, userId, e);
        }

        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalendarServiceImpl - getWeeklyOff - UserId:{} - END - ExecutionTime:{}ms",
                logId, userId, (endTime - startTime));

        return baseResponse;
    }

    @Override
    public BaseResponse<WeeklyOffDays> saveWeeklyOff(WeeklyOffRequest weeklyOffRequest) {
        long startTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " GET USERS START");
        BaseResponse<WeeklyOffDays> baseResponse = new BaseResponse<>();
        try {
            List<WeeklyOffDays> weeklyOffDaysList = weeklyOffDaysRepository.findAll();
            Map<String, String> weeklyValueMap = weeklyOffRequest.getDays().stream().collect(Collectors.toMap(k -> k, v -> v));
            weeklyOffDaysList.forEach(weeklyOffDays -> {
                if (weeklyValueMap.containsValue(weeklyOffDays.getDay())) {
                    weeklyOffDays.setIsChecked(true);
                } else {
                    weeklyOffDays.setIsChecked(false);
                }
            });

            weeklyOffDaysRepository.saveAll(weeklyOffDaysList);
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10064S);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(weeklyOffDaysList);
            baseResponse.setLogId(loginUser.getLogId());
            log.info("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage());
        } catch (Exception e) {
            ResponseMessage responseMessage = getResponseMessages(ResponseKeyConstant.UPLD10061F);
            baseResponse.setCode(responseMessage.getCode());
            baseResponse.setStatus(responseMessage.getStatus());
            baseResponse.setMessage(responseMessage.getMessage());
            baseResponse.setData(new ArrayList<>());
            baseResponse.setLogId(loginUser.getLogId());
            long endTime = System.currentTimeMillis();
            log.error("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), ResponseKeyConstant.SPACE + responseMessage.getMessage() + (endTime - startTime), e);
        }
        long endTime = System.currentTimeMillis();
        log.info("LogId:{} - CalenderServiceImpl - getUserNotAddedInShift - UserId:{} - {}", loginUser.getLogId(), loginUser.getUserId(), " FETCHED USERS LIST TIME :" + (endTime - startTime));
        return baseResponse;
    }


}


package com.sankalp.financedashboard.service;

import com.sankalp.financedashboard.dto.account.AccountDtoReduced;
import com.sankalp.financedashboard.dto.category.CategoryDto;
import com.sankalp.financedashboard.dto.record.CreateRecordRequest;
import com.sankalp.financedashboard.dto.record.RecordDto;
import com.sankalp.financedashboard.dto.record.UpdateRecordRequest;
import com.sankalp.financedashboard.entity.*;
import com.sankalp.financedashboard.entity.Record;
import com.sankalp.financedashboard.error.exception.AccountNotFoundException;
import com.sankalp.financedashboard.error.exception.CategoryNotFoundException;
import com.sankalp.financedashboard.error.exception.RecordNotFoundException;
import com.sankalp.financedashboard.error.exception.UserNotFoundException;
import com.sankalp.financedashboard.repository.RecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {
    @InjectMocks
    private RecordService recordService;
    @Mock
    private RecordRepository recordRepository;
    @Mock
    private AccountService accountService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private AuthenticationService authenticationService;

    @Test
    void getAll() {
        // when
        recordService.getAll();

        // then
        verify(recordRepository).findAll();
    }

    @Test
    void getAllFilter() throws UserNotFoundException {
        // given
        Specification<Record> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("label"),  "%restaurant%");
        Pageable pageable = PageRequest.of(0, 2);
        Long userId = 3L;

        // when
        recordService.getAllFilter(specification, pageable, userId);

        // then
        verify(recordRepository).findAll(specification, pageable);
    }

    @Test
    void getById() throws UserNotFoundException, RecordNotFoundException {
        // given
        Long recordId = 9239L;

        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .color("#6290ff")
                .icon("mdi-cash")
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));

        // when
        recordService.getById(recordId);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(recordRepository).findById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(recordId);
    }

    @Test
    void save() throws UserNotFoundException, CategoryNotFoundException, AccountNotFoundException {
        // given
        Long userId = 77L;
        User user = User.builder()
                .id(userId)
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Long currentAccountId = 22L;
        Account current = Account.builder()
                .id(currentAccountId)
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .color("#6290ff")
                .icon("mdi-cash")
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Long categoryId = 44L;
        Category category = Category.builder()
                .id(categoryId)
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .amount(9000.0)
                .label("salary")
                .note("")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        CreateRecordRequest createRequest = new CreateRecordRequest(
                9000.0,
                "salary",
                "",
                (new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime(),
                currentAccountId,
                categoryId
        );

        given(recordRepository.save(any()))
                .willReturn(record);
        given(accountService.getById(currentAccountId))
                .willReturn(current);
        given(categoryService.getById(categoryId))
                .willReturn(category);

        // when
        recordService.save(createRequest);

        // then
        ArgumentCaptor<Record> recordCapture = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository).save(recordCapture.capture());
        assertThat(recordCapture.getValue()).isEqualTo(record);
    }

    @Test
    void update()
            throws UserNotFoundException, RecordNotFoundException, CategoryNotFoundException, AccountNotFoundException {
        // given
        Long recordId = 9239L;

        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .color("#6290ff")
                .icon("mdi-cash")
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        UpdateRecordRequest updateRequest = new UpdateRecordRequest(
                recordId,
                900.0,
                "Salary",
                "",
                (new GregorianCalendar(2022, Calendar.JANUARY, 1)).getTime(),
                current.getId(),
                category.getId()
        );

        Record updatedRecord = Record.builder()
                .id(recordId)
                .amount(900.0)
                .label("Salary")
                .date((new GregorianCalendar(2022, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));
        given(recordRepository.save(updatedRecord))
                .willReturn(updatedRecord);

        // when
        Record result = recordService.update(recordId, updateRequest);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Record> updatedRecordCapture = ArgumentCaptor.forClass(Record.class);
        verify(recordRepository).findById(idCapture.capture());
        verify(recordRepository).save(updatedRecordCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(recordId);
        assertThat(updatedRecordCapture.getValue()).isEqualTo(updatedRecord);
        assertThat(result).isEqualTo(updatedRecord);
    }

    @Test
    void deleteById() throws UserNotFoundException, RecordNotFoundException {
        // given
        Long recordId = 9239L;

        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .color("#6290ff")
                .icon("mdi-cash")
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Category category = Category.builder()
                .name("Miscellaneous")
                .records(new ArrayList<>())
                .build();

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .category(category)
                .build();
        category.addRecord(record);
        current.addRecord(record);

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));

        // when
        recordService.deleteById(recordId);

        // then
        ArgumentCaptor<Long> idCapture = ArgumentCaptor.forClass(Long.class);
        verify(recordRepository).deleteById(idCapture.capture());
        assertThat(idCapture.getValue()).isEqualTo(recordId);
    }

    @Test
    void recordsToDto() {
        // given
        User user = User.builder()
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Long currentAccountId = 99L;
        Account currentAccount = Account.builder()
                .id(currentAccountId)
                .name("Current")
                .currency("USD")
                .color("#fff")
                .icon("mdi-pig")
                .balance(1000.0)
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(currentAccount);

        Long categoryId = 98989L;
        Category category = Category.builder()
                .id(categoryId)
                .name("Miscellaneous")
                .icon("icon")
                .color("#fefefe")
                .records(new ArrayList<>())
                .build();

        List<Record> records = List.of(
                Record.builder()
                        .id(1L)
                        .amount(9000.0)
                        .label("salary")
                        .date((new GregorianCalendar(2024, Calendar.JANUARY, 1)).getTime())
                        .account(currentAccount)
                        .category(category)
                        .note("")
                        .build(),
                Record.builder()
                        .id(2L)
                        .amount(-5.90)
                        .label("bike")
                        .date((new GregorianCalendar(2023, Calendar.MAY, 1)).getTime())
                        .account(currentAccount)
                        .category(category)
                        .note("")
                        .build()
        );

        AccountDtoReduced currentAccountDto = new AccountDtoReduced(
                currentAccountId,
                "Current",
                "USD",
                "#fff",
                "mdi-pig"
        );

        CategoryDto categoryDto = new CategoryDto(
                categoryId,
                "Miscellaneous",
                "icon",
                "#fefefe"
        );

        List<RecordDto> recordDtos = List.of(
                new RecordDto(
                        1L,
                        9000.0,
                        "salary",
                        "",
                        (new GregorianCalendar(2024, Calendar.JANUARY, 1)).getTime(),
                        currentAccountDto,
                       categoryDto
                ),
                new RecordDto(
                        2L,
                        -5.90,
                        "bike",
                        "",
                        (new GregorianCalendar(2023, Calendar.MAY, 1)).getTime(),
                        currentAccountDto,
                        categoryDto
                )
        );

        // when
        List<RecordDto> resultRecordDtos = RecordService.recordsToDto(records);

        // then
        assertThat(recordDtos).isEqualTo(resultRecordDtos);
    }

    // --- ANALYST role tests ---

    @Test
    void analystCanGetAllFilterWithoutUserId() throws UserNotFoundException {
        // given - ANALYST accesses all records (no userId filter)
        Specification<Record> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("label"),  "%test%");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        recordService.getAllFilter(specification, pageable, null);

        // then - verify ifNotAdminOrAnalystThrowAccessDenied was called (not the old ifNotAdminThrowAccessDenied)
        verify(authenticationService).ifNotAdminOrAnalystThrowAccessDenied();
        verify(recordRepository).findAll(specification, pageable);
    }

    @Test
    void analystCanGetAllFilterWithUserId() throws UserNotFoundException {
        // given - ANALYST accesses records for a specific user
        Specification<Record> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("label"),  "%test%");
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 5L;

        // when
        recordService.getAllFilter(specification, pageable, userId);

        // then - verify ifNotAdminOrAnalystOrSelfRequestThrowAccessDenied was called
        verify(authenticationService).ifNotAdminOrAnalystOrSelfRequestThrowAccessDenied(userId);
        verify(recordRepository).findAll(specification, pageable);
    }

    @Test
    void analystCanGetById() throws UserNotFoundException, RecordNotFoundException {
        // given
        Long recordId = 100L;
        Long userId = 77L;
        User user = User.builder()
                .id(userId)
                .firstName("Some").lastName("User")
                .email("test@gmail.com").password("secret")
                .role(Role.USER).accounts(new ArrayList<>())
                .build();
        Account account = Account.builder()
                .name("Current").currency("USD").balance(1000.0)
                .color("#6290ff").icon("mdi-cash")
                .includeInStatistic(true).user(user).records(new ArrayList<>())
                .build();
        user.addAccount(account);
        Record record = Record.builder()
                .id(recordId).amount(100.0).label("test")
                .date(new Date()).account(account)
                .build();
        account.addRecord(record);

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));

        // when
        recordService.getById(recordId);

        // then - verify the analyst-aware check was called
        verify(authenticationService).ifNotAdminOrAnalystOrSelfRequestThrowAccessDenied(userId);
    }

    @Test
    void analystCannotSave() throws UserNotFoundException, CategoryNotFoundException, AccountNotFoundException {
        // given - ANALYST tries to save, the service calls admin-or-self check on the account
        Long accountId = 22L;
        Long userId = 77L;
        User user = User.builder()
                .id(userId).firstName("Some").lastName("User")
                .email("test@gmail.com").password("secret")
                .role(Role.ANALYST).accounts(new ArrayList<>())
                .build();
        Account account = Account.builder()
                .id(accountId).name("Current").currency("USD").balance(1000.0)
                .color("#6290ff").icon("mdi-cash")
                .includeInStatistic(true).user(user).records(new ArrayList<>())
                .build();
        user.addAccount(account);

        CreateRecordRequest createRequest = new CreateRecordRequest(
                100.0, "test", "", new Date(), accountId, null
        );

        given(accountService.getById(accountId)).willReturn(account);
        // The save method calls ifNotAdminOrSelfRequestThrowAccessDenied (not analyst-aware)
        // so an ANALYST who doesn't own the account will be denied
        org.mockito.Mockito.doThrow(new org.springframework.security.access.AccessDeniedException("Access denied."))
                .when(authenticationService).ifNotAdminOrSelfRequestThrowAccessDenied(userId);

        // then - verify that save throws AccessDeniedException for ANALYST
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> recordService.save(createRequest))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    @Test
    void analystCannotDeleteById() throws UserNotFoundException {
        // given
        Long recordId = 9239L;
        Long ownerId = 77L;

        User user = User.builder()
                .id(ownerId)
                .firstName("Some")
                .lastName("User")
                .email("test.user@gmail.com")
                .password("secret")
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();

        Account current = Account.builder()
                .name("Current")
                .currency("USD")
                .balance(1000.0)
                .color("#6290ff")
                .icon("mdi-cash")
                .includeInStatistic(true)
                .user(user)
                .records(new ArrayList<>())
                .build();
        user.addAccount(current);

        Record record = Record.builder()
                .id(recordId)
                .amount(9000.0)
                .label("salary")
                .date((new GregorianCalendar(2021, Calendar.JANUARY, 1)).getTime())
                .account(current)
                .build();
        current.addRecord(record);

        given(recordRepository.findById(recordId))
                .willReturn(Optional.of(record));
        org.mockito.Mockito.doThrow(new org.springframework.security.access.AccessDeniedException("Access denied."))
                .when(authenticationService).ifNotAdminOrSelfRequestThrowAccessDenied(ownerId);

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> recordService.deleteById(recordId))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class)
                .hasMessageContaining("Access denied.");
    }
}
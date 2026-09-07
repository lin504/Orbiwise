package com.lony.orbiwise.controller;

import com.lony.orbiwise.annotation.RequirePermission;
import com.lony.orbiwise.dto.TicketDTO;
import com.lony.orbiwise.entity.Ticket;
import com.lony.orbiwise.service.TicketService;
import com.lony.orbiwise.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门票控制器
 * <p>提供门票的查询和管理接口</p>
 *
 * @author lin504
 */
@Tag(name = "门票管理", description = "门票增删改查接口")
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * 查询景点门票列表
     *
     * @param scenicId 景点ID
     * @return 门票列表
     */
    @Operation(summary = "查询门票", description = "根据景点ID查询门票列表")
    @GetMapping("/scenic/{scenicId}")
    public Result<List<Ticket>> listByScenicId(@PathVariable Long scenicId) {
        List<Ticket> tickets = ticketService.listByScenicId(scenicId);
        return Result.success(tickets);
    }

    /**
     * 新增门票（需权限）
     *
     * @param ticketDTO 门票参数
     * @return 操作结果
     */
    @Operation(summary = "新增门票", description = "管理端新增门票")
    @RequirePermission({"scenic:manage"})
    @PostMapping
    public Result<Void> create(@Valid @RequestBody TicketDTO ticketDTO) {
        ticketService.createTicket(ticketDTO);
        return Result.success();
    }

    /**
     * 编辑门票（需权限）
     *
     * @param id        门票ID
     * @param ticketDTO 门票参数
     * @return 操作结果
     */
    @Operation(summary = "编辑门票", description = "管理端编辑门票信息")
    @RequirePermission({"scenic:manage"})
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TicketDTO ticketDTO) {
        ticketService.updateTicket(id, ticketDTO);
        return Result.success();
    }

}

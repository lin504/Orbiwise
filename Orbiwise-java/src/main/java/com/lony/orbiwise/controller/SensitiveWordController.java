package com.lony.orbiwise.controller;

import com.lony.orbiwise.annotation.RequirePermission;
import com.lony.orbiwise.entity.SensitiveWord;
import com.lony.orbiwise.service.SensitiveWordService;
import com.lony.orbiwise.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 敏感词管理控制器
 * <p>提供敏感词的查询、新增、删除接口</p>
 *
 * @author lin504
 */
@Tag(name = "敏感词管理", description = "敏感词增删查接口")
@RestController
@RequestMapping("/api/sensitive")
public class SensitiveWordController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    /**
     * 敏感词列表
     *
     * @return 敏感词列表
     */
    @Operation(summary = "敏感词列表", description = "查询所有敏感词")
    @GetMapping("/list")
    public Result<List<SensitiveWord>> list() {
        List<SensitiveWord> words = sensitiveWordService.listWords();
        return Result.success(words);
    }

    /**
     * 新增敏感词
     *
     * @param sensitiveWord 敏感词实体
     * @return 操作结果
     */
    @Operation(summary = "新增敏感词", description = "新增敏感词到词库")
    @RequirePermission({"system:manage"})
    @PostMapping
    public Result<Void> add(@RequestBody SensitiveWord sensitiveWord) {
        sensitiveWordService.addWord(sensitiveWord);
        return Result.success();
    }

    /**
     * 删除敏感词
     *
     * @param id 敏感词ID
     * @return 操作结果
     */
    @Operation(summary = "删除敏感词", description = "从词库中删除敏感词")
    @RequirePermission({"system:manage"})
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sensitiveWordService.deleteWord(id);
        return Result.success();
    }

}

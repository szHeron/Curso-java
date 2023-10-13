package com.heron.TodoList.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heron.TodoList.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request){
        LocalDateTime currentDate = LocalDateTime.now();
        if(currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getStartAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data informada é inválida.");
        }
        if(task.getStartAt().isAfter(task.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data informada é inválida.");
        }
        task.setUserId((UUID) request.getAttribute("userId"));

        TaskModel createdTask = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateTask(@RequestBody TaskModel task, HttpServletRequest request, @PathVariable UUID id){
        var tempTask = this.taskRepository.findById(id).orElse(null);
        if(tempTask == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
        }

        if(!task.getUserId().equals((UUID) request.getAttribute("userId"))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado.");
        }

        Utils.copyNonNullValues(task, tempTask);
        TaskModel taskUpdated = this.taskRepository.save(tempTask);
        return ResponseEntity.ok().body(taskUpdated);
    }

    @GetMapping("/")
    public ResponseEntity getAllTasks(HttpServletRequest request){
        List<TaskModel> tasks = this.taskRepository.findByUserId((UUID) request.getAttribute("userId"));
        return ResponseEntity.status(HttpStatus.FOUND).body(tasks);
    }
}

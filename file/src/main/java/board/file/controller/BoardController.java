package board.file.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import board.file.dto.board.BoardCreateDTO;
import board.file.dto.board.BoardDTO;
import board.file.dto.board.BoardListDTO;
import board.file.dto.board.BoardNoticeCreateDTO;
import board.file.dto.board.BoardNoticeUpdateDTO;
import board.file.dto.board.BoardUpdateDTO;
import board.file.dto.page.PageRequestDTO;
import board.file.dto.page.PageResponseDTO;
import board.file.service.BoardService;
import board.file.util.ManagementCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

// Board Controller Class 
@Log4j2
@Controller
@RequestMapping("/board/")
public class BoardController {

    // 의존성 주입
    private final BoardService boardServce;
    private final ManagementCookie managementCookie;

    // Autowired 명시적 표시
    @Autowired
    public BoardController(BoardService boardService, ManagementCookie managementCookie) {
        log.info("Constructor Called, Service Injected.");
        this.boardServce = boardService;
        this.managementCookie = managementCookie;
    }

    // GET : Create
    @GetMapping("create")
    @PreAuthorize("hasAnyRole('USER')")
    public String getBoardCreate() {
        log.info("GET | Board Create");
        return "/board/create";
    }

    // GET : Create Notice 
    @GetMapping("createnotice")
    @PreAuthorize("hasAnyRole('USER')")
    public String getBoardCreateNotice() {
        log.info("GET | Board Notice Create");
        return "/board/createnotice";
    }

    // POST : Create Notice
    @PostMapping("createntoice")
    @PreAuthorize("hasAnyRole('USER')")
    public String postBoardCreateNotice(NoticeCreateDTO boardNoticeCreateDTO) {
        log.info("POST | Board Notice Create");
        boardServce.createBoardNotice(boardNoticeCreateDTO);
        return "redirect:/board/list";
    }

    // GET : Read Notice 
    @GetMapping("readnotice/{tno}")
    @PreAuthorize("hasAnyRole('USER')")
    public String getBoardReadNotice(@PathVariable("tno") Long tno, Model model, HttpServletRequest request, HttpServletResponse response) {
        log.info("GET | Board Notice Read");
        if(managementCookie.createCookie(request, response, tno)) {
            boardServce.viewCount(tno);
            log.info("Making Cookie Notice Read");
        }
        BoardDTO list = boardServce.readBoardNotice(tno);
        model.addAttribute("list", list);
        return "/board/readnotice";
    }

    // GET : Update Notice 
    @GetMapping("updatenotice/{tno}")
    public String getBoardUpdateNotice(@PathVariable("tno") Long tno, Model model) {
        log.info("GET | Board Notice Update");
        BoardDTO list = boardServce.readBoardNotice(tno);
        model.addAttribute("list", list);
        return "/board/updatenotice";
    }

    // POST : Update Notice 
    @PostMapping("updatenotice/{tno}")
    @PreAuthorize("hasAnyRole('USER')")
    public String postBoardUpdateNotice(NoticeUpdateDTO boardNoticeUpdateDTO) {
        log.info("POST | Board Notice Update");
        boardServce.updateBoardNotice(boardNoticeUpdateDTO);
        return "redirect:/board/readnotice" + boardNoticeUpdateDTO.getTno();
    }
 
    // GET : List
    @GetMapping("list")
    @PreAuthorize("hasAnyRole('USER')")
    public String getBoardList(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<BoardListDTO> list = boardServce.listboard(pageRequestDTO);
        model.addAttribute("list", list);
        log.info("GET | Board List");
        return "/board/list";
    }

    // GET : Read
    @GetMapping("read/{tno}")
    @PreAuthorize("hasAnyRole('USER')")
    public String getBoardRead(@PathVariable("tno") Long tno, Model model, HttpServletRequest request, HttpServletResponse response) {
        log.info("GET | Board Read");
        if(managementCookie.createCookie(request, response, tno)) {
            boardServce.viewCount(tno);
            log.info("Making Cookie");
        }
        BoardDTO list = boardServce.readBoard(tno);
        model.addAttribute("list", list);
        return "/board/read";
    }

    // GET : Update
    @GetMapping("update/{tno}")
    @PreAuthorize("hasAnyRole('USER')")
    public String getBoardUpdate(@PathVariable("tno") Long tno, Model model, PageRequestDTO pageRequestDTO) {
        log.info("GET | Board Update");
        BoardDTO list = boardServce.readBoard(tno);
        model.addAttribute("list", list);
        return "/board/update";
    }

    // POST : Delete
    @PostMapping("delete/{tno}")
    @PreAuthorize("hasAnyRole('USER')")
    public String postBoardDelete(@PathVariable("tno") Long tno, RedirectAttributes redirectAttributes) {
        log.info("POST | Board Delte");
        boardServce.deleteBoard(tno);
        redirectAttributes.addFlashAttribute("message", "게시물이 삭제되었습니다.");
        return "redirect:/board/list";
    }

    // POST : Update
    @PostMapping("update")
    @PreAuthorize("hasAnyRole('USER')")
    public String postBoardUpdate(BoardUpdateDTO boardUpdateDTO, RedirectAttributes redirectAttributes) {
        log.info("POST | Board Update");
        boardServce.updateBoard(boardUpdateDTO);
        redirectAttributes.addFlashAttribute("message", "게시물이 업데이트 되었습니다.");
        return "redirect:/board/read/" + boardUpdateDTO.getTno();
    }

    // POST : Create
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('USER')")
    public String postBoardCreate(BoardCreateDTO boardCreateDTO, RedirectAttributes redirectAttributes) {
        log.info("POST | Board Create");
        Long tno = boardServce.createBoard(boardCreateDTO);
        redirectAttributes.addFlashAttribute("message", boardCreateDTO.getTno() + " 번 게시물로 등록되었습니다.");
        return "redirect:/board/list";
    }
}

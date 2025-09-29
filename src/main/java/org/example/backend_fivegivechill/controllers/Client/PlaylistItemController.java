package org.example.backend_fivegivechill.controllers.Client;

import jakarta.validation.Valid;
import org.example.backend_fivegivechill.beans.PlaylistItemBean;
import org.example.backend_fivegivechill.response.DeletePlaylistItemResponse;
import org.example.backend_fivegivechill.response.PlaylistItemReponse;
import org.example.backend_fivegivechill.response.Response;
import org.example.backend_fivegivechill.services.PlaylistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class PlaylistItemController {

    @Autowired
    private PlaylistItemService playlistItemService;

    @PostMapping("/addPlaylistItem")
    public ResponseEntity<Response> addSongToPlaylist(@Valid @RequestBody PlaylistItemBean playlistItemBean) {

        String success = playlistItemService.addSongToPlaylist(playlistItemBean);

        if (success == null) {
            return ResponseEntity.ok(new Response(1, success, null));
        } else {
            return ResponseEntity.ok(new Response(0, success, null));
        }
    }

    @GetMapping("/getPlayListItem/{id}")
    public ResponseEntity<Response> getPlaylistItem(
            @Valid @PathVariable int id) {
        List<PlaylistItemReponse> success = playlistItemService.getPlayListItem(id);
        return ResponseEntity.ok(new Response(0, "Chúc con uc", success));
    }

    @DeleteMapping("/playlistItem/delete/{id}")
    public ResponseEntity<Response> deletePlaylistItem(@PathVariable int id) {
        int check = playlistItemService.deletePlaylistItem(id);
        if (check > 0) {
            return ResponseEntity.ok(new Response(0, "Xóa thành công", null));
        } else {
            return ResponseEntity.ok(new Response(1, "Xóa không thành công", null));
        }
    }

    @PostMapping("/playlistItem/delete")
    public ResponseEntity<Response> deletePlaylistItem(@RequestBody DeletePlaylistItemResponse deletePlaylistItemResponse) {
        try {
            System.out.println("IDs nhận từ frontenddddddddddddddddddddddddddddđd: " + deletePlaylistItemResponse.getIdPlaylistItems());
            boolean success = playlistItemService.deletePlaylistItems(deletePlaylistItemResponse.getIdPlaylistItems());
            if (success) {
                return ResponseEntity.ok(new Response(0, "Xóa bài hát thành công!", true));
            } else {
                return ResponseEntity.ok(new Response(1, "Xóa bài hát không thành công!", false));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response(1, "Xóa bài hát thất bại: " + e.getMessage(), null));
        }
    }

    @GetMapping("/getPlayListItemBySongAndPlaylist/{songId}/{playlistId}")
    public ResponseEntity<Response> getPlaylistItemBySongAndPlaylist(@PathVariable int songId, @PathVariable int playlistId) {
        List<PlaylistItemReponse> items = playlistItemService.getPlaylistItemBySongAndPlaylist(songId, playlistId);
        return ResponseEntity.ok(new Response(0, "OK", items));
    }



}


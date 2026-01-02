package com.Beast.notes.notes_app.dto;

import lombok.Data;

/*
 NoteDto is used to receive data from the client.
 It contains only the fields the user is allowed to send.
*/
@Data
public class NoteDto {
    private String title;
    private String content;
}


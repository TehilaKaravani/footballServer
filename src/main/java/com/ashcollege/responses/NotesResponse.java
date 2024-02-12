package com.ashcollege.responses;

import com.ashcollege.entities.Note;
import com.ashcollege.responses.models.NoteModel;

import java.util.LinkedList;
import java.util.List;

public class NotesResponse extends BasicResponse{
    private List<NoteModel> notes;

    public NotesResponse(boolean success, Integer errorCode, List<Note> noteList) {
        super(success, errorCode);
        this.notes = new LinkedList<>();
        for (Note note: noteList) {
            this.notes.add(new NoteModel(note));
        }
    }

    public List<NoteModel> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteModel> notes) {
        this.notes = notes;
    }
}

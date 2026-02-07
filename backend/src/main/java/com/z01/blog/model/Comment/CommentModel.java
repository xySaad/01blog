package com.z01.blog.model.Comment;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "comments")
public class CommentModel extends AbstractComment {

}
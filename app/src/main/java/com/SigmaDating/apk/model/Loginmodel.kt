package com.SigmaDating.apk.model

import com.example.bridegroomed.model.User


class Loginmodel(val status: Boolean,
                 val user: User,
                 val posts: List<Postdata>,
                 val message: String
) {
}
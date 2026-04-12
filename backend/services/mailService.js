import nodemailer from "nodemailer";

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "avdahana@gmail.com",
    pass: "lcwbtlwsldmerbqu"
  }
});

export const sendOtpEmail = async (email, otp) => {
  try {
    console.log("BEFORE SEND");
    const info = await transporter.sendMail({
      from: `"Scripto" <avdahana@gmail.com>`,
      to: email,
      subject: "Код",
      text: `Ваш код: ${otp}`,
    });

    console.log("EMAIL SENT:", info.messageId);
    return info;
  } catch (e) {
    console.log("EMAIL ERROR:", e);
    throw e;
  }
};
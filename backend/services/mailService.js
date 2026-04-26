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
    const info = await transporter.sendMail({
      from: `"Scripto" <avdahana@gmail.com>`,
      to: email,
      subject: `Код подтверждения: ${otp}`,
      html: `
        <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 400px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 12px; text-align: center;">
          <h2 style="color: #3F51B5; margin-bottom: 20px;">Восстановление пароля</h2>
          <p style="color: #666; font-size: 16px;">Используйте этот код, чтобы сбросить пароль в приложении <b>Scripto</b>:</p>
          
          <div style="margin: 30px 0; padding: 15px; background-color: #f5f5f5; border-radius: 8px; border: 1px dashed #3F51B5;">
            <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #3F51B5;">
              ${otp}
            </span>
          </div>
          
          <p style="color: #999; font-size: 12px; margin-top: 20px;">
            Если вы не запрашивали сброс пароля, просто проигнорируйте это письмо. Код действителен в течение 10 минут.
          </p>
          <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
          <p style="font-size: 14px; color: #3F51B5; font-weight: bold;">Команда Scripto</p>
        </div>
      `,
    });

    console.log("EMAIL SENT:", info.messageId);
    return info;
  } catch (e) {
    console.log("EMAIL ERROR:", e);
    throw e;
  }
};


export const sendVerificationEmail = async (email, token) => {
  const url = `https://scripto-app.ru${token}`;

  const mailOptions = {
    from: '"Scripto" <avdahana@gmail.com>',
    to: email,
    subject: 'Добро пожаловать в Scripto! Подтвердите ваш email',
    html: `
      <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 450px; margin: 0 auto; padding: 30px; border: 1px solid #e0e0e0; border-radius: 16px; text-align: center; background-color: #ffffff;">
        <h1 style="color: #3F51B5; margin-bottom: 10px; font-size: 24px;">Добро пожаловать!</h1>
        <p style="color: #666; font-size: 16px; line-height: 1.5;">
          Мы очень рады, что вы присоединились к <b>Scripto</b>. <br>
          Остался всего один шаг — подтвердить вашу почту.
        </p>
        
        <div style="margin: 35px 0;">
          <a href="${url}" style="background-color: #3F51B5; color: #ffffff; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 4px 6px rgba(63, 81, 181, 0.2);">
            Подтвердить почту
          </a>
        </div>
        
        <hr style="border: 0; border-top: 1px solid #eee; margin: 25px 0;">
        <p style="font-size: 14px; color: #757575;">Если вы не регистрировались в Scripto, просто удалите это письмо.</p>
      </div>
    `
  };

  return transporter.sendMail(mailOptions);
};
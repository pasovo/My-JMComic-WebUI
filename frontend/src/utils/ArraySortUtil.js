export default class ArraySortUtil{
    static compare(n1,n2){
        const c1 = n1.split('');
        const c2 = n2.split('');
        const r1 = this.charArrMerge(c1);
        const r2 = this.charArrMerge(c2);

        const min = Math.min(r1.length, r2.length);
        let i = 0;
        for (; i < min; i++) {
            if (r1[i] === r2[i]) {
                continue;
            }
            return r1[i] - r2[i];
        }

        // 前面都没有判断出来，长的排后面
        if (r1.length > r2.length) {
            return r1[i];
        } else if (r1.length === r2.length) {
            // 一样长的话，那就是两个文本权重相等
            return 0;
        } else {
            return r2[i];
        }
    }
    static charArrMerge(cArr) {
        const r1 = new Array(cArr.length);
        const r_tmp = new Array(cArr.length);
        let i_in = 0;
        let i_tmp_val = 0;

        for (let i = 0; i < cArr.length; i++) {
            const c_t = cArr[i];
            const charCode = c_t.charCodeAt(0);

            if (charCode >= 48 && charCode <= 57) {
                i_tmp_val = this.addStr(i_tmp_val, charCode);
                continue;
            } else {
                // 缓存出栈
                if (i_tmp_val > 0) {
                    r_tmp[i_in] = i_tmp_val;
                    i_tmp_val = 0;
                    i_in++;
                }

                // 在JavaScript中，字符直接作为数字比较时使用其ASCII码
                // 如果需要保持与Java相同的行为（将字符视为数字），可能需要调整
                // 这里假设我们想要比较字符本身（类似于Java的char比较）
                // 所以我们存储字符的ASCII码
                i_tmp_val = charCode;
            }

            r_tmp[i_in] = i_tmp_val;
            i_tmp_val = 0;
            i_in++;
        }

        // 缓存出栈
        if (i_tmp_val > 0) {
            r_tmp[i_in] = i_tmp_val;
            i_tmp_val = 0;
            i_in++;
        }

        if (i_in + 1 === r1.length) {
            return r_tmp;
        }

        return r_tmp.slice(0, i_in + 1);
    }
    static addStr(num, charCode) {
        const tmp = num.toString() + (charCode - 48); // 48是'0'的ASCII码
        return parseInt(tmp, 10);
    }
}
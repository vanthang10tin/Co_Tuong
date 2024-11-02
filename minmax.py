import math

# Giá trị cho cắt tỉa Alpha-Beta
INF = math.inf
DEPTH_LIMIT = 3  # Độ sâu tối đa

# Bàn cờ ban đầu với mã hóa các quân cờ
# 0 là ô trống, các số dương là quân đỏ, các số âm là quân đen.
# (1: Tướng, 2: Xe, 3: Mã, 4: Pháo, 5: Sĩ, 6: Tượng, 7: Chốt)
start_board = [
    [0, 0, 0, -5, -1, -5, 0, 0, 0],
    [0, 0, 0,  0,  0,  0, 0, 0, 0],
    [0, -3, 0,  0,  0,  0, 0, -3, 0],
    [-7, 0, -4, 0,  0,  0, -4, 0, -7],
    [0, 0, 0,  0,  0,  0, 0, 0, 0],
    [0, 0, 0,  0,  0,  0, 0, 0, 0],
    [7, 0, 4,  0,  0,  0, 4, 0, 7],
    [0, 3, 0,  0,  0,  0, 0, 3, 0],
    [0, 0, 0,  0,  0,  0, 0, 0, 0],
    [0, 0, 0,  5,  1,  5, 0, 0, 0],
]

# Hàm đánh giá bàn cờ
def eval_board(board):
    piece_score = {1: 1000, 2: 50, 3: 30, 4: 30, 5: 10, 6: 10, 7: 10}
    score = 0
    for r, row in enumerate(board):
        for c, piece in enumerate(row):
            if piece > 0:  # Quân đỏ
                score += piece_score.get(piece, 0) + pos_bonus(piece, r, c)
            elif piece < 0:  # Quân đen
                score -= piece_score.get(-piece, 0) + pos_bonus(piece, r, c)
    return score

# Hàm cộng thêm điểm dựa trên vị trí của quân cờ
def pos_bonus(piece, r, c):
    if abs(piece) == 2:  # Xe, ưu tiên đường trung tâm
        return 5 if c in [3, 4, 5] else 0
    elif abs(piece) == 3:  # Mã
        return 3 if r < 5 else 1
    elif abs(piece) == 4:  # Pháo, ưu tiên giữa
        return 5 if r in [3, 4, 5] else 0
    elif abs(piece) == 7:  # Chốt, khuyến khích vượt sông
        return 3 if (piece > 0 and r < 5) or (piece < 0 and r > 4) else 0
    return 0

# Hàm sinh nước đi hợp lệ cho từng loại quân cờ
def gen_moves(board, red_turn):
    moves = []
    for r in range(10):
        for c in range(9):
            piece = board[r][c]
            if (red_turn and piece > 0) or (not red_turn and piece < 0):
                moves.extend(gen_piece_moves(board, r, c, piece))
    return moves

# Hàm sinh nước đi cho từng loại quân cờ
def gen_piece_moves(board, r, c, piece):
    moves = []
    if abs(piece) == 1:  # Tướng
        moves.extend(gen_king_moves(board, r, c, piece))
    elif abs(piece) == 2:  # Xe
        moves.extend(gen_rook_moves(board, r, c, piece))
    elif abs(piece) == 3:  # Mã
        moves.extend(gen_knight_moves(board, r, c, piece))
    elif abs(piece) == 4:  # Pháo
        moves.extend(gen_cannon_moves(board, r, c, piece))
    elif abs(piece) == 5:  # Sĩ
        moves.extend(gen_guard_moves(board, r, c, piece))
    elif abs(piece) == 6:  # Tượng
        moves.extend(gen_elephant_moves(board, r, c, piece))
    elif abs(piece) == 7:  # Chốt
        moves.extend(gen_pawn_moves(board, r, c, piece))
    return moves

# Hàm sinh nước đi cho Tướng
def gen_king_moves(board, r, c, piece):
    moves = []
    directions = [(1, 0), (-1, 0), (0, 1), (0, -1)]
    for dr, dc in directions:
        nr, nc = r + dr, c + dc
        if 0 <= nr < 10 and 3 <= nc <= 5 and ((piece > 0 and 7 <= nr <= 9) or (piece < 0 and 0 <= nr <= 2)):
            if board[nr][nc] == 0 or (board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0):
                new_board = [row[:] for row in board]
                new_board[r][c] = 0
                new_board[nr][nc] = piece
                moves.append(new_board)
    return moves

# Hàm sinh nước đi cho Xe
def gen_rook_moves(board, r, c, piece):
    moves = []
    directions = [(1, 0), (-1, 0), (0, 1), (0, -1)]
    for dr, dc in directions:
        nr, nc = r + dr, c + dc
        while 0 <= nr < 10 and 0 <= nc < 9:
            if board[nr][nc] == 0:
                new_board = [row[:] for row in board]
                new_board[r][c] = 0
                new_board[nr][nc] = piece
                moves.append(new_board)
            elif (board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0):
                new_board = [row[:] for row in board]
                new_board[r][c] = 0
                new_board[nr][nc] = piece
                moves.append(new_board)
                break
            else:
                break
            nr += dr
            nc += dc
    return moves

# Hàm sinh nước đi cho Sĩ
def gen_guard_moves(board, r, c, piece):
    moves = []
    guard_moves = [(1, 1), (1, -1), (-1, 1), (-1, -1)]
    for dr, dc in guard_moves:
        nr, nc = r + dr, c + dc
        if 0 <= nr < 10 and 3 <= nc <= 5 and ((piece > 0 and 7 <= nr <= 9) or (piece < 0 and 0 <= nr <= 2)):
            if board[nr][nc] == 0 or (board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0):
                new_board = [row[:] for row in board]
                new_board[r][c] = 0
                new_board[nr][nc] = piece
                moves.append(new_board)
    return moves

# Hàm sinh nước đi cho Tượng với quy tắc "Tượng bị cản"
def gen_elephant_moves(board, r, c, piece):
    moves = []
    elephant_moves = [(2, 2), (2, -2), (-2, 2), (-2, -2)]
    for dr, dc in elephant_moves:
        nr, nc = r + dr, c + dc
        br, bc = r + dr // 2, c + dc // 2
        if 0 <= nr < 10 and 0 <= nc < 9 and ((piece > 0 and nr >= 5) or (piece < 0 and nr <= 4)):
            if board[br][bc] == 0 and (board[nr][nc] == 0 or (board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0)):
                new_board = [row[:] for row in board]
                new_board[r][c] = 0
                new_board[nr][nc] = piece
                moves.append(new_board)
    return moves

# Hàm sinh nước đi cho Chốt
def gen_pawn_moves(board, r, c, piece):
    moves = []
    directions = [(-1, 0)] if piece > 0 else [(1, 0)]
    if (piece > 0 and r < 5) or (piece < 0 and r > 4):
        directions.extend([(0, 1), (0, -1)])

    for dr, dc in directions:
        nr, nc = r + dr, c + dc
        if 0 <= nr < 10 and 0 <= nc < 9:
            if board[nr][nc] == 0 or (board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0):
                new_board = [row[:] for row in board]
                new_board[r][c] = 0
                new_board[nr][nc] = piece
                moves.append(new_board)
    return moves

# Hàm sinh nước đi cho quân Mã, bao gồm quy tắc "Mã bị cản"
def gen_knight_moves(board, r, c, piece):
    moves = []
    knight_moves = [
        (2, 1, 1, 0), (2, -1, 1, 0), (-2, 1, -1, 0), (-2, -1, -1, 0),
        (1, 2, 0, 1), (-1, 2, 0, 1), (1, -2, 0, -1), (-1, -2, 0, -1)
    ]
    for dr, dc, br, bc in knight_moves:
        nr, nc = r + dr, c + dc
        block_r, block_c = r + br, c + bc
        if 0 <= nr < 10 and 0 <= nc < 9:
            if board[block_r][block_c] == 0:
                if board[nr][nc] == 0 or (board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0):
                    new_board = [row[:] for row in board]
                    new_board[r][c] = 0
                    new_board[nr][nc] = piece
                    moves.append(new_board)
    return moves

# Hàm sinh nước đi cho Pháo
def gen_cannon_moves(board, r, c, piece):
    moves = []
    directions = [(1, 0), (-1, 0), (0, 1), (0, -1)]
    for dr, dc in directions:
        nr, nc = r + dr, c + dc
        while 0 <= nr < 10 and 0 <= nc < 9 and board[nr][nc] == 0:
            new_board = [row[:] for row in board]
            new_board[r][c] = 0
            new_board[nr][nc] = piece
            moves.append(new_board)
            nr += dr
            nc += dc
        nr += dr
        nc += dc
        while 0 <= nr < 10 and 0 <= nc < 9:
            if board[nr][nc] != 0:
                if board[nr][nc] < 0 if piece > 0 else board[nr][nc] > 0:
                    new_board = [row[:] for row in board]
                    new_board[r][c] = 0
                    new_board[nr][nc] = piece
                    moves.append(new_board)
                break
            nr += dr
            nc += dc
    return moves

# Hàm Minimax với cắt tỉa Alpha-Beta
def minimax(board, depth, alpha, beta, red_turn):
    if depth == DEPTH_LIMIT or is_game_end(board):
        return eval_board(board)
    if red_turn:
        max_eval = -INF
        for move in gen_moves(board, red_turn):
            eval = minimax(move, depth + 1, alpha, beta, False)
            max_eval = max(max_eval, eval)
            alpha = max(alpha, eval)
            if beta <= alpha:
                break
        return max_eval
    else:
        min_eval = INF
        for move in gen_moves(board, red_turn):
            eval = minimax(move, depth + 1, alpha, beta, True)
            min_eval = min(min_eval, eval)
            beta = min(beta, eval)
            if beta <= alpha:
                break
        return min_eval

# Hàm kiểm tra điều kiện kết thúc trò chơi
def is_game_end(board):
    red_king, black_king = False, False
    for row in board:
        for piece in row:
            if piece == 1:
                red_king = True
            elif piece == -1:
                black_king = True
    return not (red_king and black_king)

# Chạy chương trình với bàn cờ ban đầu
if __name__ == "__main__":
    print("Điểm đánh giá của bàn cờ:", minimax(start_board, 0, -INF, INF, True))
